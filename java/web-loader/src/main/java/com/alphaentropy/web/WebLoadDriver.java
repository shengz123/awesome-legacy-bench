package com.alphaentropy.web;

import com.alphaentropy.common.utils.DateTimeUtil;
import com.alphaentropy.web.eastmoney.EastMoneyLoadDriver;
import com.alphaentropy.web.hexun.HexunLoadDriver;
import com.alphaentropy.web.netease.NetEaseLoadDriver;
import com.alphaentropy.web.sina.SinaLoadDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class WebLoadDriver {
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);

    @Autowired
    private NetEaseLoadDriver netEaseLoadDriver;

    @Autowired
    private EastMoneyLoadDriver eastMoneyLoadDriver;

    @Autowired
    private HexunLoadDriver hexunLoadDriver;

    @Autowired
    private SinaLoadDriver sinaLoadDriver;

    public void loadDaily(List<String> symbols) {
        try {
            String today = sdf.format(new Date());
            String[] quarterDates = DateTimeUtil.getReportPeriod();

            // Hexun has DDos protection policy
            hexunLoadDriver.loadDaily(symbols);
            eastMoneyLoadDriver.loadDaily(today, symbols);

            if (DateTimeUtil.isWeekend()) {
                eastMoneyLoadDriver.loadWeekly(today, symbols);
                sinaLoadDriver.load(symbols);
            }

            // It's during the release reporting period
            if (quarterDates != null && quarterDates.length > 0) {
                for (String reportDate : quarterDates) {
                    eastMoneyLoadDriver.loadQuarter(reportDate, symbols);
                }
                netEaseLoadDriver.loadQuarterly(symbols);
            }
        } catch (Exception e) {
            log.error("Failed to load web", e);
        }
    }

    public void loadAllTime(List<String> symbols, Date day1) {
        try {
            List<String> allDates = DateTimeUtil.getWeekDaysSince(day1, YYYY_MM_DD);
            for (String dailyDate : allDates) {
                eastMoneyLoadDriver.onDemandByDailyDate(dailyDate);
            }
            eastMoneyLoadDriver.onDemandOneOff(symbols);

            List<String> allReportDates = DateTimeUtil.getReportDatesSince(day1, YYYY_MM_DD);
            for (String reportDate : allReportDates) {
                eastMoneyLoadDriver.loadQuarter(reportDate, symbols);
            }

            // Hexun has DDos protection policy
            hexunLoadDriver.loadDaily(symbols);
            sinaLoadDriver.load(symbols);
            netEaseLoadDriver.loadQuarterly(symbols);
        } catch (Exception e) {
            log.error("Failed to load web for all time", e);
        }
    }

}
