package com.alphaentropy.factor.infra;

import com.alphaentropy.factor.infra.compile.SourceCompiler;
import com.alphaentropy.factor.infra.script.GroovyScriptExecutor;
import com.alphaentropy.store.application.MySQLStatement;
import com.alphaentropy.store.cache.AccessorCache;
import com.alphaentropy.store.cache.DataAccessor;
import com.alphaentropy.store.infra.hbase.FactorBase;
import com.alphaentropy.store.infra.hbase.QuarterFactorBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(SpringRunner.class)
@PrepareForTest({FormulaExecution.class})
class FormulaExecutionTest {

    @Autowired
    private DataAccessor accessor;

    @Autowired
    private GroovyScriptExecutor executor;

    @Test
    void execute() throws ParseException {
        DataAccessor spyAccessor = PowerMockito.spy(accessor);
        PowerMockito.doAnswer(invocationOnMock -> {
            String symbol = invocationOnMock.getArgument(0);
            Date date = invocationOnMock.getArgument(1);
            Map<String, Object> multiValues = invocationOnMock.getArgument(2);
            String frequency = invocationOnMock.getArgument(3);
            System.out.println(symbol + " " + date + " " + frequency);
            for (String key : multiValues.keySet()) {
                System.out.println(key + ": " + multiValues.get(key));
            }
            return null;
        } ).when(spyAccessor).put(anyString(), any(), anyMap(), anyString());

        String symbol = "600519";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2022-08-01");
        String DAILY = "formula";
        ExecutionContext ctx = new ExecutionContext(Collections.singletonList(symbol), date);
        File[] dailyFiles = getFormulaFiles(DAILY);
        for (File dailyFile : dailyFiles) {
            new FormulaExecution(ctx, executor, spyAccessor).execute(new SourceCompiler().compile(dailyFile));
        }
    }

    private File[] getFormulaFiles(String path) {
        String dailyFolder = FactorCalDriver.class.getClassLoader().getResource(path).getPath();
        return new File(dailyFolder).listFiles();
    }
}