package com.alphaentropy.processor;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BasicProcessorDriver {
    @Autowired
    private AdjustFactorProcessor adjustFactorProcessor;

    @Autowired
    private SharesProcessor sharesProcessor;

    public void routine(List<String> symbols) {
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            sharesProcessor.process(symbols);
            adjustFactorProcessor.process(symbols);
            log.info("Finish post process basic data {}", stopwatch.elapsed(TimeUnit.MINUTES));
        } catch (Exception e) {
            log.error("Failed to post process basic data", e);
        }
    }
}
