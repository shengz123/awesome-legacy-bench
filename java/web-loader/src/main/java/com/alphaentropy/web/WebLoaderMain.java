package com.alphaentropy.web;

import com.alphaentropy.web.sina.SinaLoadDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.alphaentropy.domain",
        "com.alphaentropy.store",
        "com.alphaentropy.web",
})
public class WebLoaderMain {
    @Autowired
    private SinaLoadDriver driver;

    public static void main(String[] args) {
        SpringApplication.run(WebLoaderMain.class);
    }

}
