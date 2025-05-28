package com.alphaentropy.web.eastmoney;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class EastMoneyLoadDriver {
    @Autowired
    private EastMoneyDailyLoadDriver dailyLoadDriver;

    @Autowired
    private EastMoneyWeeklyLoadDriver weeklyLoadDriver;

    @Autowired
    private EastMoneyQuarterlyLoadDriver quarterlyLoadDriver;

    public void loadQuarter(String reportDate, List<String> symbols) {
        quarterlyLoadDriver.loadQuarter(reportDate, symbols);
    }

    public void loadWeekly(String date, List<String> symbols) {
        weeklyLoadDriver.routine(date, symbols);
    }

    public void loadDaily(String date, List<String> symbols) {
        dailyLoadDriver.routine(date, symbols);
    }

    public void onDemandByDailyDate(String date) {
        dailyLoadDriver.onDemandByDate(date);
    }

    public void onDemandOneOff(List<String> symbols) {
        dailyLoadDriver.onDemandOneOff();
        weeklyLoadDriver.onDemandOneOff(symbols);
    }

}
