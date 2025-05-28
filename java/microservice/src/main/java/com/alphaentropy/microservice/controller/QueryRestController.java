package com.alphaentropy.microservice.controller;

import com.alphaentropy.query.domain.QueryParam;
import com.alphaentropy.query.domain.QueryResponse;
import com.alphaentropy.query.interfaces.QueryController;
import com.alphaentropy.query.interfaces.rest.AiQueryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class QueryRestController implements QueryController {

    @Autowired
    private AiQueryController aiQueryController;

    @Override
    @RequestMapping(value = "/{symbol}", method = RequestMethod.POST)
    @ResponseBody
    public QueryResponse query(@PathVariable("symbol") String symbol,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        @RequestBody QueryParam param) {
        return aiQueryController.query(symbol, start, end, param);
    }

    @Override
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public QueryResponse search(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date valueDate,
                         @RequestBody QueryParam param) {
        return aiQueryController.search(valueDate, param);
    }
}
