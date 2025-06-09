package com.alphaentropy.web.sina.loader;

import org.springframework.stereotype.Component;

@Component
public class SinaTotalSharesLoader extends AbstractSinaPerSymbolWebLoader {
    private static final String URL = "http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_StockStructureHistory/stockid/XXXXXX/stocktype/TotalStock.phtml";

    @Override
    protected String getSubDumpFolder() {
        return "total_shs";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }
}
