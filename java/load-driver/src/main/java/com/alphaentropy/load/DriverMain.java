package com.alphaentropy.load;

import com.alphaentropy.load.driver.LoadDriver;
import com.alphaentropy.processor.SharesProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.alphaentropy.domain",
        "com.alphaentropy.store",
        "com.alphaentropy.web",
        "com.alphaentropy.tdx",
        "com.alphaentropy.load",
        "com.alphaentropy.factor",
        "com.alphaentropy.processor",
})
public class DriverMain {
    @Autowired
    private LoadDriver driver;

    public static void main(String[] args) {
        SpringApplication.run(DriverMain.class);
    }
}
