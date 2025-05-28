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
public class ProfitRaw {
    @MySQLSymbolKey
    private String symbol;
    @MySQLDateKey
    private Date reportDate;

    //营业总收入(万元)
    private BigDecimal totalBizRevenue;
    //营业收入(万元)
    private BigDecimal bizRevenue;
    //利息收入(万元)
    private BigDecimal interestRevenue;
    //已赚保费(万元)
    private BigDecimal insuranceRevenue;
    //手续费及佣金收入(万元)
    private BigDecimal commissionRevenue;
    //房地产销售收入(万元)
    private BigDecimal realEstateSaleRevenue;
    //其他业务收入(万元)
    private BigDecimal otherRevenue;

    //营业总成本(万元)
    private BigDecimal totalBizCost;
    //营业成本(万元)
    private BigDecimal bizCost;
    //利息支出(万元)
    private BigDecimal interestCost;
    //手续费及佣金支出(万元)
    private BigDecimal commissionCost;
    //房地产销售成本(万元)
    private BigDecimal realEstateSaleCost;
    //研发费用(万元)
    private BigDecimal rndCost;
    //退保金(万元)
    private BigDecimal insuranceRefund;
    //赔付支出净额(万元)
    private BigDecimal claimCostNet;
    //提取保险合同准备金净额(万元)
    private BigDecimal insuranceReserveNet;
    //保单红利支出(万元)
    private BigDecimal insuranceYieldPayment;
    //分保费用(万元)
    private BigDecimal reinsuranceExpense;
    //其他业务成本(万元)
    private BigDecimal otherCost;
    //营业税金及附加(万元)
    private BigDecimal bizTax;
    //销售费用(万元)
    private BigDecimal saleExpense;
    //管理费用(万元)
    private BigDecimal mgmtExpense;
    //财务费用(万元)
    private BigDecimal fundingExpense;
    //资产减值损失(万元)
    private BigDecimal assetLossReserve;
    //公允价值变动收益(万元)
    private BigDecimal fairValueChangeEarn;
    //投资收益(万元)
    private BigDecimal investEarn;
    //对联营企业和合营企业的投资收益(万元)
    private BigDecimal investEarnCoop;
    //汇兑收益(万元)
    private BigDecimal fxPnl;
    //期货损益(万元)
    private BigDecimal futurePnl;
    //托管收益(万元)
    private BigDecimal custodianEarn;
    //补贴收入(万元)
    private BigDecimal compensation;
    //其他业务利润(万元)
    private BigDecimal otherProfit;
    //营业利润(万元)
    private BigDecimal bizProfit;

    //营业外收入(万元)
    private BigDecimal nonBizRevenue;
    //营业外支出(万元)
    private BigDecimal nonBizCost;
    //非流动资产处置损失(万元)
    private BigDecimal nonLiqAssetTreatedLoss;
    //利润总额(万元)
    private BigDecimal totalProfit;
    //所得税费用(万元)
    private BigDecimal incomeTaxExpense;
    //未确认投资损失(万元)
    private BigDecimal investLossYetRealized;
    //净利润(万元)
    private BigDecimal netProfit;
    //归属于母公司所有者的净利润(万元)
    private BigDecimal netProfitBelonging;
    //被合并方在合并前实现净利润(万元)
    private BigDecimal netProfitPreMerge;
    //少数股东损益(万元)
    private BigDecimal minorShareHolderPnl;
    //基本每股收益
    private BigDecimal earnPerShare;
    //稀释每股收益
    private BigDecimal dilutedEarnPerShare;
}
