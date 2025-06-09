package com.alphaentropy.tdx.reference;

import com.alphaentropy.common.utils.CSVDataFrame;
import com.alphaentropy.common.utils.SymbolUtil;
import com.alphaentropy.domain.reference.Industry;
import com.alphaentropy.store.application.MySQLStatement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class IndustryLoader {
    @Value("${industry_mapping_file}")
    private String MAPPING_FILE;
    @Value("${industry_file}")
    private String DATA_FILE;

    private static final Map<Integer, String> indexCatalogMapping = new HashMap<>();
    private static final int MAPPING_NUM_COLS = 4;
    private static final String DELIMITER = "\\|";

    private Map<String, Map<String, String>> catalogIndustryMappings = new HashMap<>();

    static {
        indexCatalogMapping.put(2, "#TDXNHY");
        indexCatalogMapping.put(5, "#TDXRSHY");
    }

    public List load() {
        CSVDataFrame mapping = CSVDataFrame.fromFile(new File(MAPPING_FILE), DELIMITER, 0, MAPPING_NUM_COLS);
        CSVDataFrame data = CSVDataFrame.fromFile(new File(DATA_FILE), DELIMITER, 0, -1);
        Map<String, CSVDataFrame> industriesMap = data.splitAsMap(1);
        List list = new ArrayList();
        for (int i = 0; i < mapping.numRows(); i++) {
            String[] symbolMapping = mapping.getRow(i);
            for (int index : indexCatalogMapping.keySet()) {
                String symbol = symbolMapping[1];
                String catalog = indexCatalogMapping.get(index);
                String code = symbolMapping[index];
                Map<String, String> industryCodeNames = catalogIndustryMappings.get(catalog);
                if (industryCodeNames == null) {
                    industryCodeNames = getIndustryMapping(industriesMap.get(catalog));
                    catalogIndustryMappings.put(catalog, industryCodeNames);
                }

                if (SymbolUtil.isAShare(symbol)) {
                    list.add(createIndustry(symbol, catalog, code, industryCodeNames));
                }
            }
        }
        return list;
    }

    private Industry createIndustry(String symbol, String catalog, String code,
                                    Map<String, String> industryCodeNames) {
        Industry industry = new Industry();
        industry.setSymbol(symbol);
        industry.setCatalog(catalog.replace("#", ""));
        industry.setLevel2Code(code);
        industry.setLevel2Name(industryCodeNames.get(code));
        industry.setLevel1Code(code.substring(0, 3));
        industry.setLevel1Name(industryCodeNames.get(industry.getLevel1Code()));
        return industry;
    }

    private Map<String, String> getIndustryMapping(CSVDataFrame df) {
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < df.numRows(); i++) {
            String[] row = df.getRow(i);
            result.put(row[0], row[1]);
        }
        return result;
    }
}
