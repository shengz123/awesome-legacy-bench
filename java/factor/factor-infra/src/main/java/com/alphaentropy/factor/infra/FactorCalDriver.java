package com.alphaentropy.factor.infra;

import com.alphaentropy.common.utils.CSVDataFrame;
import com.alphaentropy.common.utils.DateTimeUtil;
import com.alphaentropy.factor.infra.compile.SourceCompiler;
import com.alphaentropy.factor.infra.script.GroovyScriptExecutor;
import com.alphaentropy.store.cache.DataAccessor;
import com.alphaentropy.tdx.TdxLoadDriver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class FactorCalDriver {

    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String DAILY = "formula/daily";
    private static final String QUARTERLY = "formula/quarterly";
    private static final String SELECT_SYMBOL = "selectSymbols/universe.csv";

    @Autowired
    private DataAccessor accessor;

    @Autowired
    private GroovyScriptExecutor executor;

    @Autowired
    private TdxLoadDriver tdxLoadDriver;

    private void loadSelectedSymbols() throws ParseException {
        String filepath=FactorCalDriver.class.getClassLoader().getResource(SELECT_SYMBOL).getPath();
        CSVDataFrame dataFrame=CSVDataFrame.fromFile(new File(filepath),",",1,-1);
//        get the symbol list
        List<String> symbols = Arrays.asList(dataFrame.getColumn(0));
        onDemandAllHistory(symbols, new SimpleDateFormat("yyyy-MM-dd").parse("2010-01-01"));
    }

    @PostConstruct
    private void init() throws Exception {
//        loadSelectedSymbols();
//        Set<String> symbols = new HashSet<>();
//        symbols.add("600276");
//        symbols.add("600289");
//        symbols.add("301047");
//        onDemandAllHistory(symbols, new SimpleDateFormat("yyyy-MM-dd").parse("2021-01-01"));
    }

    private File[] getDailyFormulaFiles() {
        return getFormulaFiles(DAILY);
    }

    private File[] getQuarterlyFormulaFiles() {
        return getFormulaFiles(QUARTERLY);
    }

    private File[] getFormulaFiles(String path) {
        String dailyFolder = FactorCalDriver.class.getClassLoader().getResource(path).getPath();
        return new File(dailyFolder).listFiles();
    }

    public void loadDateFile(Collection<String> symbols, Date valueDate, File formulaFile) {
        ExecutionContext ctx = new ExecutionContext(symbols, valueDate);
        new FormulaExecution(ctx, executor, accessor).execute(new SourceCompiler().compile(formulaFile));
    }

    public void loadDailyHistory(Collection<String> symbols, List<Date> allDates) {
        for (Date date : allDates) {
            ExecutionContext ctx = new ExecutionContext(symbols, date);
            File[] dailyFiles = getDailyFormulaFiles();
            for (File dailyFile : dailyFiles) {
                new FormulaExecution(ctx, executor, accessor).execute(new SourceCompiler().compile(dailyFile));
            }
        }
    }

    public void loadQuarterlyHistory(Collection<String> symbols, List<Date> allDates) {
        for (Date date : allDates) {
            ExecutionContext ctx = new ExecutionContext(symbols, date);
            File[] quarterlyFiles = getQuarterlyFormulaFiles();
            for (File quarterlyFile : quarterlyFiles) {
                new FormulaExecution(ctx, executor, accessor).execute(new SourceCompiler().compile(quarterlyFile));
            }
        }
    }

    public void onDemandAllHistory(Collection<String> symbols, Date day1) {
        List<Date> allReportDates = DateTimeUtil.getReportDatesSince(day1);
        loadQuarterlyHistory(symbols, allReportDates);

        List<Date> allDailyDates = DateTimeUtil.getWeekDaysSince(day1);
        loadDailyHistory(symbols, allDailyDates);
    }
}
