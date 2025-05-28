package com.alphaentropy.query.interfaces;

import com.alphaentropy.query.application.QueryService;
import com.alphaentropy.query.domain.QueryParam;
import com.alphaentropy.query.domain.QueryResponse;
import com.alphaentropy.query.domain.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@Slf4j
@Lazy
public class QueryControllerImpl implements QueryController {

    @Autowired
    private QueryService queryService;

    @Override
    public QueryResponse query(String symbol, Date start, Date end, QueryParam param) {
        int responseCode = ResponseCode.SUCCESS;
        Object result = null;
        if (param == null) {
            responseCode = ResponseCode.BAD_REQUEST;
        } else {
            List<String> returnedColumns = param.getReturnedColumns();
            TreeMap<Date, Map<String, Object>> ret = queryService.query(symbol, start, end, returnedColumns);
            result = convert(ret, symbol);
        }
        return QueryResponse.builder()
                .responseCode(responseCode)
                .message(ResponseCode.getMessage(responseCode))
                .result(result)
                .build();
    }

    private Map<String, List<Map<String, Object>>> convert(TreeMap<Date, Map<String, Object>> treeMap, String symbol) {
        Map<String, List<Map<String, Object>>> ret = new HashMap<>();
        List<Map<String, Object>> values = new ArrayList<>();
        ret.put(symbol, values);
        treeMap.forEach((date, stringObjectMap) -> {
            Map<String, Object> res = new HashMap<>(stringObjectMap);
            res.put("valueDate", date);
            values.add(res);
        });
        return ret;
    }

    @Override
    public QueryResponse search(Date valueDate, QueryParam param) {
        int responseCode = ResponseCode.SUCCESS;
        Object result = null;
        if (param == null) {
            responseCode = ResponseCode.BAD_REQUEST;
        } else {
            String condition = param.getCondition();
            List<String> returnedColumns = param.getReturnedColumns();
            Map<String, Map<String, Object>> res = queryService.search(valueDate, condition, returnedColumns);
            result = convert(res, valueDate);
        }
        return QueryResponse.builder()
                .responseCode(responseCode)
                .message(ResponseCode.getMessage(responseCode))
                .result(result)
                .build();
    }

    private Map<String, List<Map<String, Object>>> convert(Map<String, Map<String, Object>> res, Date valueDate) {
        Map<String, List<Map<String, Object>>> ret = new HashMap<>();
        String strValueDate = new SimpleDateFormat("yyyy-MM-dd").format(valueDate);
        res.forEach((s, stringObjectMap) -> {
            stringObjectMap.put("valueDate", strValueDate);
            List<Map<String, Object>> values = new ArrayList<>();
            values.add(stringObjectMap);
            ret.put(s, values);
        });
        return ret;
    }
}
