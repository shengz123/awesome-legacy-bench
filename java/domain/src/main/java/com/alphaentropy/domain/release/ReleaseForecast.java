package com.alphaentropy.domain.release;

import com.alphaentropy.domain.annotation.*;
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
public class ReleaseForecast {
    @MySQLSymbolKey
    private String securityCode;

    private Date noticeDate;

    @MySQLDateKey
    private Date reportDate;
    private String predictFinance;

    @MySQLVarchar("128")
    private String predictContent;
    @MySQLVarchar("2048")
    private String changeReasonExplain;
    private String predictType;

    //预测下限
    private BigDecimal predictAmtLower;
    //预测上限
    private BigDecimal predictAmtUpper;
    //预测百分比下限
    private BigDecimal addAmpLower;
    //预测百分比上限
    private BigDecimal addAmpUpper;
    //上一期值
    private BigDecimal preyearSamePeriod;
    //变化百分比中间值
    private BigDecimal increaseJz;
    //变化中间值
    private BigDecimal forecastJz;
    //预告类型
    private String forecastState;

}
