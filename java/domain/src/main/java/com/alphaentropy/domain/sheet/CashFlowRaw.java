package com.alphaentropy.domain.sheet;

import com.alphaentropy.domain.annotation.*;
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
@Cached(frequency = "low", effectiveClz = ReleaseDate.class)
public class CashFlowRaw {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date reportDate;

    //销售商品、提供劳务收到的现金(万元)
    private BigDecimal saleReceived;
    //客户存款和同业存放款项净增加额(万元)
    private BigDecimal depositReceived;
    //向中央银行借款净增加额(万元)
    private BigDecimal borrowCentralBank;
    //向其他金融机构拆入资金净增加额(万元)
    private BigDecimal borrowOtherFinInst;
    //收到原保险合同保费取得的现金(万元)
    private BigDecimal insuranceReceived;
    //收到再保险业务现金净额(万元)
    private BigDecimal reinsuranceReceived;
    //保户储金及投资款净增加额(万元)
    private BigDecimal insuranceDepositReceived;
    //处置交易性金融资产净增加额(万元)
    private BigDecimal finAssetTreatmentReceived;
    //收取利息、手续费及佣金的现金(万元)
    private BigDecimal interestCommissionReceived;
    //拆入资金净增加额(万元)
    private BigDecimal borrowReceived;
    //回购业务资金净增加额(万元)
    private BigDecimal repoReceived;
    //收到的税费返还(万元)
    private BigDecimal taxRefund;
    //收到的其他与经营活动有关的现金(万元)
    private BigDecimal otherBizReceived;
    //经营活动现金流入小计(万元)
    private BigDecimal totalBizCashFlowIn;

    //购买商品、接受劳务支付的现金(万元)
    private BigDecimal productServicePaid;
    //客户贷款及垫款净增加额(万元)
    private BigDecimal loanPaid;
    //存放中央银行和同业款项净增加额(万元)
    private BigDecimal depositCentralBank;
    //支付原保险合同赔付款项的现金(万元)
    private BigDecimal insurancePaid;
    //支付利息、手续费及佣金的现金(万元)
    private BigDecimal interestCommissionPaid;
    //支付保单红利的现金(万元)
    private BigDecimal insuranceYieldPaid;
    //支付给职工以及为职工支付的现金(万元)
    private BigDecimal salaryPaid;
    //支付的各项税费(万元)
    private BigDecimal taxPaid;
    //支付的其他与经营活动有关的现金(万元)
    private BigDecimal otherBizPaid;
    //经营活动现金流出小计(万元)
    private BigDecimal totalBizCashFlowOut;

    //经营活动产生的现金流量净额(万元)
    private BigDecimal totalBizCashFlowNet;

    //收回投资所收到的现金(万元)
    private BigDecimal investQuitReceived;
    //取得投资收益所收到的现金(万元)
    private BigDecimal investEarnReceived;
    //处置固定资产、无形资产和其他长期资产所收回的现金净额(万元)
    private BigDecimal fixedInvisibleAssetSold;
    //处置子公司及其他营业单位收到的现金净额(万元)
    private BigDecimal childEntitySold;
    //收到的其他与投资活动有关的现金(万元)
    private BigDecimal otherInvestReceived;
    //减少质押和定期存款所收到的现金(万元)
    private BigDecimal pledgeDepositWithDrawn;
    //投资活动现金流入小计(万元)
    private BigDecimal totalInvestCashFlowIn;

    //购建固定资产、无形资产和其他长期资产所支付的现金(万元)
    private BigDecimal fixedInvisibleAssetBought;
    //投资所支付的现金(万元)
    private BigDecimal investPaid;
    //质押贷款净增加额(万元)
    private BigDecimal pledgeLoanIncreased;
    //取得子公司及其他营业单位支付的现金净额(万元)
    private BigDecimal childEntityBought;
    //支付的其他与投资活动有关的现金(万元)
    private BigDecimal otherInvestPaid;
    //增加质押和定期存款所支付的现金(万元)
    private BigDecimal pledgeDepositPaid;
    //投资活动现金流出小计(万元)
    private BigDecimal totalInvestCashFlowOut;

    //投资活动产生的现金流量净额(万元)
    private BigDecimal totalInvestCashFlowNet;

    //吸收投资收到的现金(万元)
    private BigDecimal cashReceivedFromInvest;
    //其中：子公司吸收少数股东投资收到的现金(万元)
    private BigDecimal cashReceivedFromInvestChildEntity;
    //取得借款收到的现金(万元)
    private BigDecimal cashReceivedFromBorrow;
    //发行债券收到的现金(万元)
    private BigDecimal cashReceivedFromBond;
    //收到其他与筹资活动有关的现金(万元)
    private BigDecimal cashReceivedFromOthers;
    //筹资活动现金流入小计(万元)
    private BigDecimal totalFundingCashFlowIn;

    //偿还债务支付的现金(万元)
    private BigDecimal debtPaid;
    //分配股利、利润或偿付利息所支付的现金(万元)
    private BigDecimal yieldInterestPaid;
    //其中：子公司支付给少数股东的股利、利润(万元)
    private BigDecimal yieldInterestPaidChildEntity;
    //支付其他与筹资活动有关的现金(万元)
    private BigDecimal cashPaidForOthers;
    //筹资活动现金流出小计(万元)
    private BigDecimal totalFundingCashFlowOut;
    //筹资活动产生的现金流量净额(万元)
    private BigDecimal totalFundingCashFlowNet;

    //汇率变动对现金及现金等价物的影响(万元)
    private BigDecimal fxChangeImpact;
    //现金及现金等价物净增加额(万元)
    private BigDecimal cashEquivIncreased;
    //加:期初现金及现金等价物余额(万元)
    private BigDecimal cashEquivBalanceStart;
    //期末现金及现金等价物余额(万元)
    private BigDecimal cashEquivBalanceEnd;
    //净利润(万元)
    private BigDecimal netProfit;
    //少数股东损益(万元)
    private BigDecimal minorShareHolderPnl;
    //未确认的投资损失(万元)
    private BigDecimal investLossYetRealized;
    //资产减值准备(万元)
    private BigDecimal assetLossReserve;
    //固定资产折旧、油气资产折耗、生产性物资折旧(万元)
    private BigDecimal fixedOilGasBioAssetDepreciationReserve;
    //无形资产摊销(万元)
    private BigDecimal invisibleAssetAmortization;
    //长期待摊费用摊销(万元)
    private BigDecimal longTermExpenseAmortization;
    //待摊费用的减少(万元)
    private BigDecimal amortizedExpenseDecreased;
    //预提费用的增加(万元)
    private BigDecimal advancedExpenseIncreased;
    //处置固定资产、无形资产和其他长期资产的损失(万元)
    private BigDecimal fixedVisibleLongTermAssetTreatedLoss;
    //固定资产报废损失(万元)
    private BigDecimal fixedAssetDecoLoss;
    //公允价值变动损失(万元)
    private BigDecimal fairValueChangeLoss;
    //递延收益增加(减：减少)(万元)
    private BigDecimal deferIncomeChange;
    //预计负债(万元)
    private BigDecimal advancedLiability;
    //财务费用(万元)
    private BigDecimal fundingExpense;
    //投资损失(万元)
    private BigDecimal investLoss;
    //递延所得税资产减少(万元)
    private BigDecimal deferredIncomeTaxAssetLoss;
    //递延所得税负债增加(万元)
    private BigDecimal deferredIncomeTaxLiabilityIncrease;
    //存货的减少(万元)
    private BigDecimal inventoryDecrease;
    //经营性应收项目的减少(万元)
    private BigDecimal bizReceivableDecrease;
    //经营性应付项目的增加(万元)
    private BigDecimal bizPayableIncrease;
    //已完工尚未结算款的减少(减:增加)(万元)
    private BigDecimal completedUnsettled;
    //已结算尚未完工款的增加(减:减少)(万元)
    private BigDecimal incompleteSettled;
    //其他(万元)
    private BigDecimal others;
    //经营活动产生现金流量净额(万元)
    private BigDecimal totalBizCashFlowNet1;
    //债务转为资本(万元)
    private BigDecimal liabilityConvertedToAsset;
    //一年内到期的可转换公司债券(万元)
    private BigDecimal convertibleMature1Y;
    //融资租入固定资产(万元)
    //现金的期末余额(万元)
    private BigDecimal currencyStart;
    //现金的期初余额(万元)
    private BigDecimal currencyEnd;
    //现金等价物的期末余额(万元)
    private BigDecimal currencyEqvStart;
    //现金等价物的期初余额(万元)
    private BigDecimal currencyEqvEnd;
    //现金及现金等价物的净增加额(万元)
    private BigDecimal currencyChanged;

}
