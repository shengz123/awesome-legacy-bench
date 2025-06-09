package com.alphaentropy.store.infra.mysql;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MySQLConfig {
    @Value("${jdbc_url}")
    public String JDBC_URL;
    @Value("${jdbc_driver}")
    public String MYSQL_DRIVER;
    @Value("${jdbc_username}")
    public String JDBC_USER;
    @Value("${jdbc_password}")
    public String JDBC_PW;
}
