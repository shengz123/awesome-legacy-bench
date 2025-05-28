package com.alphaentropy.web.eastmoney.mapper;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.alphaentropy.common.utils.BeanUtil.mapRowToBean;

@Slf4j
public abstract class EastMoneyPlainMapper {
    private static final String DELIMITER = ",";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String NA = "-";

    abstract public String trimContent(String content);

    public List map(String content, Class clz) {
        content = trimContent(content);
        List ret = new ArrayList();
        Gson gson = new Gson();
        List<String> list = gson.fromJson(content, List.class);
        for (String line : list) {
            String[] row = line.split(DELIMITER);
            try {
                Object value = mapRowToBean(row, clz, YYYY_MM_DD, true, null, null, NA, false);
                if (value != null) {
                    ret.add(value);
                }
            } catch (Exception e) {
                log.error("Failed to map object " + clz.getName(), e);
            }
        }
        return ret;
    }
}
