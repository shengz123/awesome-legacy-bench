package com.alphaentropy.web.sina.loader;

import org.springframework.stereotype.Component;

@Component
public class SinaLimitedSharesLoader extends AbstractSinaPerSymbolWebLoader {
    private static final String URL = "http://vip.stock.finance.sina.com.cn/corp/go.php/vCI_StockStructureHistory/stockid/XXXXXX/stocktype/XianShouA.phtml";

    @Override
    protected String getSubDumpFolder() {
        return "limited_shs";
    }

    @Override
    protected String getUrlRoot() {
        return URL;
    }
}
