package com.alphaentropy.schema;

import com.alphaentropy.schema.mysql.DDL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.alphaentropy.schema",
        "com.alphaentropy.domain",
        "com.alphaentropy.store"
})
public class DBSchemaMain {
    @Autowired
    private DDL ddl;

    public static void main(String[] args) {
        SpringApplication.run(DBSchemaMain.class);
    }
}
