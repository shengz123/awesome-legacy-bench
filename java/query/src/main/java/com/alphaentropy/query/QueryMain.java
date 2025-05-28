package com.alphaentropy.query;


import com.alphaentropy.query.application.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.alphaentropy.query",
        "com.alphaentropy.domain",
        "com.alphaentropy.store"
})
public class QueryMain {
    @Autowired
    private QueryService queryService;

    public static void main(String[] args) {
        SpringApplication.run(QueryMain.class);
    }
}
