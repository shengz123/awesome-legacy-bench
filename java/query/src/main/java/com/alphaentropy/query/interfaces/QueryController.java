package com.alphaentropy.query.interfaces;

import com.alphaentropy.query.domain.QueryParam;
import com.alphaentropy.query.domain.QueryResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RequestMapping("/data")
public interface QueryController {
    @RequestMapping(value = "/{symbol}", method = RequestMethod.POST)
    @ResponseBody
    QueryResponse query(@PathVariable("symbol") String symbol,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
                        @RequestBody QueryParam param);

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    QueryResponse search(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date valueDate,
                         @RequestBody QueryParam param);
}
