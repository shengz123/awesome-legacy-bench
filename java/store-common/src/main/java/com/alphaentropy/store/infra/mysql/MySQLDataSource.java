package com.alphaentropy.store.infra.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.alphaentropy.store.infra.mysql.MySQLConfig.*;

@Configuration
public class MySQLDataSource {

    @Autowired
    private MySQLConfig mySQLConfig;

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(mySQLConfig.MYSQL_DRIVER);
        dataSource.setUrl(mySQLConfig.JDBC_URL);
        dataSource.setUsername(mySQLConfig.JDBC_USER);
        dataSource.setPassword(mySQLConfig.JDBC_PW);
        dataSource.setInitialSize(1);
        dataSource.setMaxTotal(50);
        dataSource.setMinIdle(1);
        dataSource.setMaxIdle(10);
        dataSource.setMaxWaitMillis(20 * 1000);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setTimeBetweenEvictionRunsMillis(60 * 1000);
        return dataSource;
    }
}
