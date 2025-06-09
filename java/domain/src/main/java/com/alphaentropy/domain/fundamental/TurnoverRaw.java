package com.alphaentropy.domain.fundamental;

import com.alphaentropy.domain.annotation.Cached;
import com.alphaentropy.domain.annotation.MySQLDateKey;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLTable;
import com.alphaentropy.domain.release.ReleaseDate;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@Setter
@Getter
@MySQLTable
@NoArgsConstructor
@AllArgsConstructor
@Cached(frequency = "quarterly", effectiveClz = ReleaseDate.class)
public class TurnoverRaw {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date reportDate;
    //应收账款周转率(次)
    private BigDecimal receivableTurnoverRatio;
    //应收账款周转天数(天)
    private BigDecimal receivableTurnoverDays;
    //存货周转率(次)
    private BigDecimal inventoryTurnoverRatio;
    //固定资产周转率(次)
    private BigDecimal fixAssetTurnoverRatio;
    //总资产周转率(次)
    private BigDecimal totalAssetTurnoverRatio;
    //存货周转天数(天)
    private BigDecimal inventoryTurnoverDays;
    //总资产周转天数(天)
    private BigDecimal totalAssetTurnoverDays;
    //流动资产周转率(次)
    private BigDecimal liqAssetTurnoverRatio;
    //流动资产周转天数(天)
    private BigDecimal liqAssetTurnoverDays;
    //经营现金净流量对销售收入比率(%)
    private BigDecimal bizCashFlowNetToSalesRatio;
    //资产的经营现金流量回报率(%)
    private BigDecimal returnByBizCashFlowNetOnAsset;
    //经营现金净流量与净利润的比率(%)
    private BigDecimal bizCashFlowToNetProfitRatio;
    //经营现金净流量对负债比率(%)
    private BigDecimal bizCashFlowNetToLiabilityRatio;
    //现金流量比率(%)
    private BigDecimal cashFlowRatio;
}
