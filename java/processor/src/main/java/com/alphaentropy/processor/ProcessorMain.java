package com.alphaentropy.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.alphaentropy.domain",
        "com.alphaentropy.store",
        "com.alphaentropy.processor",
})
public class ProcessorMain {
    @Autowired
    private AdjustFactorProcessor processor;

    public static void main(String[] args) {
        SpringApplication.run(ProcessorMain.class);
    }
}
