package com.alphaentropy.tdx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.alphaentropy.domain",
        "com.alphaentropy.store",
        "com.alphaentropy.tdx",
})
public class TdxLoaderMain {
    @Autowired
    private TdxLoadDriver loadDriver;

    public static void main(String[] args) {
        SpringApplication.run(TdxLoaderMain.class);
    }
}
