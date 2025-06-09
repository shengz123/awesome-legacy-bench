package com.alphaentropy.web.eastmoney.mapper;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

import static com.alphaentropy.common.utils.BeanUtil.mapDataFrameToBeans;

public class EastMoneyJsonMapper {

    private Class clz;

    private String[] keys;

    private String dateFmt;
    public EastMoneyJsonMapper(Class clz, String[] keys, String dateFmt) {
        this.clz = clz;
        this.keys = keys;
        this.dateFmt = dateFmt;
    }

    public List map(String content, String dateFmt) {
        if (dateFmt == null) {
            dateFmt = this.dateFmt;
        }
        Gson gson = new Gson();
        Map map = gson.fromJson(content, Map.class);
        List data = null;
        for (int i = 0; i < keys.length; i++) {
            if (map != null && map.containsKey(keys[i])) {
                if (i < keys.length - 1) {
                    map = (Map) map.get(keys[i]);
                } else {
                    data = (List) map.get(keys[i]);
                }
            } else {
                return null;
            }
        }
        return mapDataFrameToBeans(data, clz, dateFmt, true, null, null, null);
    }
}
