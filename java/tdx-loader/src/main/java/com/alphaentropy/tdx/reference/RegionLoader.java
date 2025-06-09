package com.alphaentropy.tdx.reference;

import com.alphaentropy.common.utils.CSVDataFrame;
import com.alphaentropy.common.utils.SymbolUtil;
import com.alphaentropy.domain.reference.Region;
import com.alphaentropy.store.application.MySQLStatement;
import com.alphaentropy.tdx.dbf.DBFReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Component
@Slf4j
public class RegionLoader {
    @Value("${region_mapping_file}")
    private String MAPPING_FILE;
    @Value("${region_file}")
    private String DATA_FILE;

    private static final String DELIMITER = "\\|";

    public List load() {
        try {
            Map<String, String> symbolRegionMap = parseDBF();
            Map<String, String> codeRegionMapping = parseRegionCodeMapping(CSVDataFrame.fromFile(new File(DATA_FILE),
                    DELIMITER, 0, -1));
            return getSymbolRegions(symbolRegionMap, codeRegionMapping);
        } catch (Exception e) {
            log.error("Failed to load the region", e);
        }
        return null;
    }

    public List getAllSymbols() {
        try {
            Map<String, String> symbolRegionMap = parseDBF();
            TreeSet<String> set = new TreeSet<>();
            for (String symbol : symbolRegionMap.keySet()) {
                if (SymbolUtil.isAShare(symbol)) {
                    set.add(symbol);
                }
            }
            return new ArrayList(set);
        } catch (Exception e) {
            log.error("Failed to load the symbols of region", e);
        }
        return null;
    }

    private List getSymbolRegions(Map<String, String> symbolRegionMap, Map<String, String> codeRegionMapping) {
        List ret = new ArrayList();
        for (String symbol : symbolRegionMap.keySet()) {
            if (SymbolUtil.isAShare(symbol)) {
                ret.add(new Region(symbol, codeRegionMapping.get(symbolRegionMap.get(symbol))));
            }
        }
        return ret;
    }

    private Map<String, String> parseRegionCodeMapping(CSVDataFrame dataFrame) throws Exception {
        int count = 0;
        Map<String, String> codeRegionMapping = new HashMap<>();
        for (int i = 0; i < dataFrame.numRows(); i++) {
            String[] row = dataFrame.getRow(i);
            codeRegionMapping.put(row[5], row[0].replace("板块", ""));
            if (++count > 31) {
                break;
            }
        }
        return codeRegionMapping;
    }

    private Map<String, String> parseDBF() throws Exception {
        Map<String, String> symbolRegionMap = new HashMap<String, String>();
        InputStream fis = new FileInputStream(MAPPING_FILE);
        DBFReader reader = new DBFReader(fis);
        Object[] rowValues;
        while ((rowValues = reader.nextRecord()) != null) {
            String symbol = null;
            String region = null;
            for (int i = 0; i < rowValues.length; i++) {
                if (i == 1) {
                    symbol = rowValues[i].toString();
                }
                if (i == 34) {
                    region = rowValues[i].toString();
                }
                if (symbol != null && region != null) {
                    symbolRegionMap.put(symbol.trim(), region.trim());
                }
            }
        }
        fis.close();
        return symbolRegionMap;
    }
}
