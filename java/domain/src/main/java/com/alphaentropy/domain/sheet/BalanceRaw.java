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
public class BalanceRaw {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date reportDate;

    //货币资金(万元)
    private BigDecimal currency;
    //结算备付金(万元)
    private BigDecimal settlementReserve;
    //拆出资金(万元)
    private BigDecimal lending;
    //交易性金融资产(万元)
    private BigDecimal finAssetForTrade;
    //衍生金融资产(万元)
    private BigDecimal derivedFinAsset;
    //应收票据(万元)
    private BigDecimal receivableNote;
    //应收账款(万元)
    private BigDecimal receivable;
    //预付款项(万元)
    private BigDecimal advancedPay;
    //应收保费(万元)
    private BigDecimal receivableInsurance;
    //应收分保账款(万元)
    private BigDecimal receivableReinsurance;
    //应收分保合同准备金(万元)
    private BigDecimal receivableReinsuranceReserve;
    //应收利息(万元)
    private BigDecimal receivableInterest;
    //应收股利(万元)
    private BigDecimal receivableYield;
    //其他应收款(万元)
    private BigDecimal receivableOthers;
    //应收出口退税(万元)
    private BigDecimal receivableExportTaxRebate;
    //应收补贴款(万元)
    private BigDecimal receivableSubsidy;
    //应收保证金(万元)
    private BigDecimal receivableMargin;
    //内部应收款(万元)
    private BigDecimal receivableInternal;
    //买入返售金融资产(万元)
    private BigDecimal buyingBack;
    //存货(万元)
    private BigDecimal inventory;
    //待摊费用(万元)
    private BigDecimal deferredExpense;
    //待处理流动资产损益(万元)
    private BigDecimal pendingLiqAssetPnl;
    //一年内到期的非流动资产(万元)
    private BigDecimal nearTermMaturedNonLiqAsset;
    //其他流动资产(万元)
    private BigDecimal otherLiqAsset;
    //流动资产合计(万元)
    private BigDecimal totalLiqAsset;

    //发放贷款及垫款(万元)
    private BigDecimal issuedLoan;
    //可供出售金融资产(万元)
    private BigDecimal finAssetForSale;
    //持有至到期投资(万元)
    private BigDecimal investHeldTillMature;
    //长期应收款(万元)
    private BigDecimal receivableLongTerm;
    //长期股权投资(万元)
    private BigDecimal receivableEquityInvest;
    //其他长期投资(万元)
    private BigDecimal otherLongTermInvest;
    //投资性房地产(万元)
    private BigDecimal houseForInvest;
    //固定资产原值(万元)
    private BigDecimal fixedAssetOrig;
    //累计折旧(万元)
    private BigDecimal cumulativeDepreciation;
    //固定资产净值(万元)
    private BigDecimal fixedAssetNet;
    //固定资产减值准备(万元)
    private BigDecimal fixedAssetDepreciationReserve;
    //固定资产(万元)
    private BigDecimal fixedAsset;
    //在建工程(万元)
    private BigDecimal projectUnderConstruction;
    //工程物资(万元)
    private BigDecimal projectMaterial;
    //固定资产清理(万元)
    private BigDecimal fixedAssetClean;
    //生产性生物资产(万元)
    private BigDecimal bioAssetProduce;
    //公益性生物资产(万元)
    private BigDecimal bioAssetCharity;
    //油气资产(万元)
    private BigDecimal oilGasAsset;
    //无形资产(万元)
    private BigDecimal invisibleAsset;
    //开发支出(万元)
    private BigDecimal devPay;
    //商誉(万元)
    private BigDecimal goodWill;
    //长期待摊费用(万元)
    private BigDecimal deferredExpenseLongTerm;
    //股权分置流通权(万元)
    private BigDecimal shareRestructureLiqRight;
    //递延所得税资产(万元)
    private BigDecimal deferredIncomeTaxAsset;
    //其他非流动资产(万元)
    private BigDecimal otherNonLiqAsset;
    //非流动资产合计(万元)
    private BigDecimal totalNonLiqAsset;

    //资产总计(万元)
    private BigDecimal totalAsset;

    //短期借款(万元)
    private BigDecimal borrowShortTerm;
    //向中央银行借款(万元)
    private BigDecimal borrowCentralBank;
    //吸收存款及同业存放(万元)
    private BigDecimal depositFromInterBank;
    //拆入资金(万元)
    private BigDecimal borrows;
    //交易性金融负债(万元)
    private BigDecimal finLiabilityForTrade;
    //衍生金融负债(万元)
    private BigDecimal derivedFinLiability;
    //应付票据(万元)
    private BigDecimal payableNote;
    //应付账款(万元)
    private BigDecimal payable;
    //预收账款(万元)
    private BigDecimal advancedReceive;
    //卖出回购金融资产款(万元)
    private BigDecimal repoSell;
    //应付手续费及佣金(万元)
    private BigDecimal payableCommission;
    //应付职工薪酬(万元)
    private BigDecimal payableSalary;
    //应交税费(万元)
    private BigDecimal payableTax;
    //应付利息(万元)
    private BigDecimal payableInterest;
    //应付股利(万元)
    private BigDecimal payableYield;
    //其他应交款(万元)
    private BigDecimal payableOther1;
    //应付保证金(万元)
    private BigDecimal payableMargin;
    //内部应付款(万元)
    private BigDecimal payableInternal;
    //其他应付款(万元)
    private BigDecimal payableOther2;
    //预提费用(万元)
    private BigDecimal advancedExpense;
    //预计流动负债(万元)
    private BigDecimal advancedLiqLiability;
    //应付分保账款(万元)
    private BigDecimal payableReinsurance;
    //保险合同准备金(万元)
    private BigDecimal insuranceContractReserve;
    //代理买卖证券款(万元)
    private BigDecimal agentSecurityTradeDeposit;
    //代理承销证券款(万元)
    private BigDecimal agentUnderwriteTradeDeposit;
    //国际票证结算(万元)
    private BigDecimal internationalNoteSettlement;
    //国内票证结算(万元)
    private BigDecimal domesticNoteSettlement;
    //递延收益(万元)
    private BigDecimal deferredRevenue;
    //应付短期债券(万元)
    private BigDecimal payableBondShortTerm;
    //一年内到期的非流动负债(万元)
    private BigDecimal nonLiqLiabilityMature1Y;
    //其他流动负债(万元)
    private BigDecimal liqLiabilityOthers;
    //流动负债合计(万元)
    private BigDecimal totalLiqLiability;

    //长期借款(万元)
    private BigDecimal borrowLongTerm;
    //应付债券(万元)
    private BigDecimal payableBond;
    //长期应付款(万元)
    private BigDecimal payableLongTerm;
    //专项应付款(万元)
    private BigDecimal payableSpecific;
    //预计非流动负债(万元)
    private BigDecimal advancedNonLiqLiability;
    //长期递延收益(万元)
    private BigDecimal deferredRevenueLongTerm;
    //递延所得税负债(万元)
    private BigDecimal deferredIncomeTaxLiability;
    //其他非流动负债(万元)
    private BigDecimal nonLiqLiabilityOthers;
    //非流动负债合计(万元)
    private BigDecimal totalNonLiqLiability;

    //负债合计(万元)
    private BigDecimal totalLiability;

    //实收资本(或股本)(万元)
    private BigDecimal paidInCapital;
    //资本公积(万元)
    private BigDecimal capitalReserve;
    //减:库存股(万元)
    private BigDecimal sharesInventory;
    //专项储备(万元)
    private BigDecimal specialSavings;
    //盈余公积(万元)
    private BigDecimal profitReserve;
    //一般风险准备(万元)
    private BigDecimal regularRiskReserve;
    //未确定的投资损失(万元)
    private BigDecimal investLossYetRealized;
    //未分配利润(万元)
    private BigDecimal profitYetOffered;
    //拟分配现金股利(万元)
    private BigDecimal cashDividendYetOffered;
    //外币报表折算差额(万元)
    private BigDecimal fxSheetDiscountedNet;
    //归属于母公司股东权益合计(万元)
    private BigDecimal totalShareHolderEquityBelonging;
    //少数股东权益(万元)
    private BigDecimal minorShareHolderEquity;
    //所有者权益(或股东权益)合计(万元)
    private BigDecimal totalOwnerShareHolderEquity;
    //负债和所有者权益(或股东权益)总计(万元)
    private BigDecimal liabilityAndTotalOwnerShareHolderEquity;

}
