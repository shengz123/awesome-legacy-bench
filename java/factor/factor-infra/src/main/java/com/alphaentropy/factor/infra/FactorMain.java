package com.alphaentropy.factor.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.alphaentropy.domain",
        "com.alphaentropy.store",
        "com.alphaentropy.tdx",
        "com.alphaentropy.factor",
})
public class FactorMain {
    @Autowired
    private FactorCalDriver driver;

    public static void main(String[] args) {
        SpringApplication.run(FactorMain.class);
    }
}
