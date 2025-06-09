package com.alphaentropy.domain.offer;


import com.alphaentropy.domain.annotation.MySQLDateKey;
import com.alphaentropy.domain.annotation.MySQLSymbolKey;
import com.alphaentropy.domain.annotation.MySQLTable;
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
public class ConvertibleOffer {
    @MySQLSymbolKey
    private String convertStockCode;

    private String securityCode;
    //上市日期
    @MySQLDateKey
    private Date listingDate;
    //起息日,申购日期
    private Date valueDate;
    //转股开始日
    private Date transferStartDate;
    //转股结束日
    private Date transferEndDate;
    //转股价
    private BigDecimal initialTransferPrice;

    private String rating;
}
