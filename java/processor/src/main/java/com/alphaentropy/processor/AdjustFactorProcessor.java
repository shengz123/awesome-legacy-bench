package com.alphaentropy.processor;

import com.alphaentropy.common.utils.LoadFilter;
import com.alphaentropy.common.utils.SymbolUtil;
import com.alphaentropy.domain.basic.*;
import com.alphaentropy.domain.offer.RightOffer;
import com.alphaentropy.store.application.MySQLStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AdjustFactorProcessor {

    @Autowired
    private MySQLStatement statement;

    @PostConstruct
    private void init() throws Exception {
//        List<String> symbols = new ArrayList<>();
//        symbols.add("301047");
//        process(symbols);
    }

    public void process(List<String> symbols) throws Exception {
        Map<String, Date> latestDates = statement.getMostRecent(RawAdjustFactor.class);
        Map<String, Date> ipoDates = statement.getMostRecent(IpoDate.class);
        ExecutorService symbolExecutorService = Executors.newFixedThreadPool(20);
        for (String symbol : symbols) {
            if (!SymbolUtil.isAShare(symbol)) {
                continue;
            }
            symbolExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        processSymbol(symbol, latestDates, ipoDates.get(symbol));
                    } catch (Exception e) {
                        log.error("Failed to process adjust factors for {}", symbol, e);
                    }
                }
            });
        }
        symbolExecutorService.shutdown();
        symbolExecutorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.HOURS);
    }

    private void processSymbol(String symbol, Map<String, Date> latestDatesRaw, Date ipoDate) {
        List<HexunCorpAction> corpActions = statement.queryBySymbol(HexunCorpAction.class, symbol, true);
        List<RightOffer> rightOffers = statement.queryBySymbol(RightOffer.class, symbol, true);
        List<Date> dates = getDateRequiresClose(symbol, corpActions, rightOffers);

        Map<Date, HexunCorpAction> corpActionMap = convertCorpActions(corpActions);
        Map<Date, RightOffer> rightOfferMap = convertRightOffer(rightOffers);
        Map<Date, DailyPrice> closes = getCloses(dates, symbol);
        TreeMap<Date, RawAdjustFactor> rawAFMap = new TreeMap<>();
        List<RawAdjustFactor> rawAdjustFactors = new ArrayList<>();

        for (Date date : closes.keySet()) {
            if (corpActionMap.containsKey(date) && rightOfferMap.containsKey(date)) {
                BigDecimal af = getAdjFactorByBothSplitAndRight(closes.get(date), corpActionMap.get(date),
                        rightOfferMap.get(date));
                if (af != null) {
                    rawAFMap.put(date, new RawAdjustFactor(symbol, corpActionMap.get(date).getEffectiveDate(), af));
                }
            } else if (corpActionMap.containsKey(date)) {
                BigDecimal afSplit = getAdjFactorBySplit(closes.get(date), corpActionMap.get(date));
                if (afSplit != null) {
                    rawAFMap.put(date, new RawAdjustFactor(symbol, corpActionMap.get(date).getEffectiveDate(), afSplit));
                }
            } else if (rightOfferMap.containsKey(date)) {
                BigDecimal afRight = getAdjFactorByRightOffer(closes.get(date), rightOfferMap.get(date));
                if (afRight != null) {
                    rawAFMap.put(date, new RawAdjustFactor(symbol, rightOfferMap.get(date).getExDividendDate(), afRight));
                }
            }
        }

        for (Date date : rawAFMap.keySet()) {
            rawAdjustFactors.add(rawAFMap.get(date));
        }

        List rawAFToAdd = LoadFilter.filter(rawAdjustFactors, latestDatesRaw, RawAdjustFactor.class);
        if (rawAFToAdd != null && !rawAFToAdd.isEmpty()) {
            statement.batchInsert(rawAFToAdd, RawAdjustFactor.class);
            Map<Date, BigDecimal> forward = getForward(ipoDate, rawAFMap);

            if (forward != null && !forward.isEmpty()) {
                statement.deleteBySymbol(ForwardAdjustFactor.class, symbol);
                statement.batchInsert(getForwardList(symbol, forward), ForwardAdjustFactor.class);
            }

            Map<Date, BigDecimal> backward = getBackward(ipoDate, rawAFMap);
            if (backward != null && !backward.isEmpty()) {
                statement.deleteBySymbol(BackwardAdjustFactor.class, symbol);
                statement.batchInsert(getBackwardList(symbol, backward), BackwardAdjustFactor.class);
            }
        }
    }

    private Map<Date, BigDecimal> getForward(Date ipoDate, TreeMap<Date, RawAdjustFactor> rawAFMap) {
        List<Date> forwardDates = new ArrayList<>();
        forwardDates.add(ipoDate);
        rawAFMap.forEach((d, r) -> forwardDates.add(r.getEffectDate()));
        List<BigDecimal> forwardFactors = new ArrayList<>();
        forwardFactors.add(new BigDecimal(1));
        for (RawAdjustFactor factor : rawAFMap.values()) {
            forwardFactors.add(factor.getFactor());
        }
        return cumulativeFactor(forwardDates, forwardFactors);
    }

    private Map<Date, BigDecimal> getBackward(Date ipoDate, TreeMap<Date, RawAdjustFactor> rawAFMap) {
        List<Date> backwardDates = new ArrayList<>();
        rawAFMap.descendingMap().forEach((d, r) -> backwardDates.add(r.getEffectDate()));
        backwardDates.add(ipoDate);
        List<BigDecimal> backwardFactors = new ArrayList<>();
        backwardFactors.add(new BigDecimal(1));
        for (RawAdjustFactor factor : rawAFMap.descendingMap().values()) {
            backwardFactors.add(factor.getFactor());
        }
        return cumulativeFactor(backwardDates, backwardFactors);
    }

    private List<ForwardAdjustFactor> getForwardList(String symbol, Map<Date, BigDecimal> forward) {
        List<ForwardAdjustFactor> result = new ArrayList<>();
        for (Date date : forward.keySet()) {
            result.add(new ForwardAdjustFactor(symbol, date, forward.get(date)));
        }
        return result;
    }

    private List<BackwardAdjustFactor> getBackwardList(String symbol, Map<Date, BigDecimal> backward) {
        List<BackwardAdjustFactor> result = new ArrayList<>();
        for (Date date : backward.keySet()) {
            result.add(new BackwardAdjustFactor(symbol, date, backward.get(date)));
        }
        return result;
    }

    private Map<Date, BigDecimal> cumulativeFactor(List<Date> dates, List<BigDecimal> factors) {
        Map<Date, BigDecimal> result = new TreeMap<>();
        BigDecimal cumulative = new BigDecimal(1);

        for (int i = 0; i < dates.size(); i++) {
            cumulative = cumulative.multiply(factors.get(i));
            result.put(dates.get(i), cumulative);
        }
        return result;
    }

    private Map<Date, BigDecimal> cumulativeFactor(TreeMap<Date, RawAdjustFactor> rawAFMap, Set<Date> dates,
                                                   Date ipoDate, boolean backward) {
        Map<Date, BigDecimal> result = new TreeMap<>();
        BigDecimal cumulative = new BigDecimal(1);
        if (!backward) {
            result.put(ipoDate, cumulative);
        }
        int counter = 0;
        for (Date date : dates) {
            if (backward) {

            }
            RawAdjustFactor rawAF = rawAFMap.get(date);
            cumulative = cumulative.multiply(rawAF.getFactor());
            result.put(rawAF.getEffectDate(), cumulative);
        }
        if (backward) {
            result.put(ipoDate, cumulative);
        }
        return result;
    }

    private Map<Date, DailyPrice> getCloses(List<Date> dates, String symbol) {
        Map<Date, DailyPrice> closes = new TreeMap<>();
        for (Date date : dates) {
            DailyPrice close = (DailyPrice) statement.queryBySymbolDate(DailyPrice.class, symbol, date);
            if (close != null) {
                closes.put(date, close);
            }
        }
        return closes;
    }

    private Map<Date, HexunCorpAction> convertCorpActions(List<HexunCorpAction> corpActions) {
        Map<Date, HexunCorpAction> result = new TreeMap<>();
        BigDecimal zero = new BigDecimal(0);
        for (HexunCorpAction corpAction : corpActions) {
            if (corpAction.getRightDate() != null
                    && (corpAction.getStockDividend().compareTo(zero) > 0
                    || corpAction.getCashDividend().compareTo(zero) > 0
                    || corpAction.getStockSplit().compareTo(zero) > 0)) {
                result.put(corpAction.getRightDate(), corpAction);
            }
        }
        return result;
    }

    private Map<Date, RightOffer> convertRightOffer(List<RightOffer> rightOffers) {
        Map<Date, RightOffer> result = new TreeMap<>();
        for (RightOffer rightOffer : rightOffers) {
            if (rightOffer.getEquityRecordDate() != null) {
                result.put(rightOffer.getEquityRecordDate(), rightOffer);
            }
        }
        return result;
    }

    private List<Date> getDateRequiresClose(String symbol, List<HexunCorpAction> corpActions,
                                            List<RightOffer> rightOffers) {
        List<Date> ret = new ArrayList<>();
        BigDecimal zero = new BigDecimal(0);
        for (HexunCorpAction corpAction : corpActions) {
            if (corpAction.getRightDate() != null &&
                    (corpAction.getStockDividend().compareTo(zero) > 0
                    || corpAction.getCashDividend().compareTo(zero) > 0
                    || corpAction.getStockSplit().compareTo(zero) > 0)) {
                ret.add(corpAction.getRightDate());
            }
        }
        for (RightOffer rightOffer : rightOffers) {
            if (rightOffer.getEquityRecordDate() != null) {
                ret.add(rightOffer.getEquityRecordDate());
            }
        }
        Set<Date> set = new HashSet<>();
        set.addAll(ret);
        if (set.size() != ret.size()) {
            log.warn("Overlapped date in corp action and right offer {}", symbol);
        }
        return ret;
    }

    // The total market capital before/after right offer and split should be the same
    // new price * new total share = old price * old total share + issued amount - total cash dividend
    // adjust factor = old price / new price
    // new total share = total shares after
    // issued amount = issue price * issued shares
    // issued shares = total shares after - total shares before * split ratio
    private BigDecimal getAdjFactorByBothSplitAndRight(DailyPrice rightDatePrice, HexunCorpAction corpAction,
                                                       RightOffer rightOffer) {
        BigDecimal splitRatio = getAdjFactorBySplit(rightDatePrice, corpAction);
        BigDecimal issuedShares = rightOffer.getTotalSharesAfter().subtract(rightOffer.getTotalSharesBefore());
        BigDecimal issuedAmt = rightOffer.getIssuePrice().multiply(issuedShares);
        BigDecimal totalEquity = rightDatePrice.getClose()
                .multiply(rightOffer.getTotalSharesBefore().divide(splitRatio, 5, BigDecimal.ROUND_HALF_UP))
                .add(issuedAmt).subtract(corpAction.getTotalCashDividend());
        return rightDatePrice.getClose()
                .multiply(rightOffer.getTotalSharesAfter())
                .divide(totalEquity, 5, BigDecimal.ROUND_HALF_UP);
    }

    // adjust factor: old price / new price
    // new price = (old price - cash div) / split ratio
    // adjust factor: old price * split ratio / (old price - cash div)
    private BigDecimal getAdjFactorBySplit(DailyPrice rightDatePrice, HexunCorpAction corpAction) {
        if (rightDatePrice == null) {
            return null;
        }
        BigDecimal stockFactor = new BigDecimal(0);
        if (corpAction != null) {
            stockFactor = corpAction.getStockSplit().add(corpAction.getStockDividend());
        }
        return rightDatePrice.getClose()
                .multiply(stockFactor.add(new BigDecimal(1)))
                .divide(rightDatePrice.getClose().subtract(corpAction.getCashDividend()),
                        5, BigDecimal.ROUND_HALF_UP);
    }

    // factor: (shs_after * equity_record_date_price) / (equity_record_date_price * shs_before + issue_price * issue_shs)
    // issue_shs: shs_after - shs_before
    private BigDecimal getAdjFactorByRightOffer(DailyPrice equityRecordDatePrice, RightOffer rightOffer) {
        if (equityRecordDatePrice == null) {
            return null;
        }
        BigDecimal issuedShs = rightOffer.getTotalSharesAfter().subtract(rightOffer.getTotalSharesBefore());
        BigDecimal issuedAmt = rightOffer.getIssuePrice().multiply(issuedShs);
        return equityRecordDatePrice.getClose()
                .multiply(rightOffer.getTotalSharesAfter())
                .divide(equityRecordDatePrice.getClose()
                        .multiply(rightOffer.getTotalSharesBefore())
                        .add(issuedAmt), 5, BigDecimal.ROUND_HALF_UP);
    }

}
