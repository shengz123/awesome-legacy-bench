package com.alphaentropy.schema.mysql;

import com.alphaentropy.common.utils.MySQLAnnotationUtil;
import com.alphaentropy.domain.annotation.MySQLTable;
import com.alphaentropy.domain.annotation.NoRecreate;
import com.alphaentropy.domain.basic.*;
import com.alphaentropy.domain.release.ReleaseDate;
import com.alphaentropy.store.application.MySQLStatement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class DDL {
    @Autowired
    private MySQLStatement mysql;

    @PostConstruct
    public void init() {
        try {
//            createTable(RawAdjustFactor.class);
//            createTable(BackwardAdjustFactor.class);
//            createTable(ReleaseDate.class);
            createTable();
        } catch (Exception e) {
            log.error("Failed to create table", e);
        }
    }

    public void createTable(Class clz) throws Exception {
        List<String> statements = MySQLAnnotationUtil.createTableStatements(clz, null, true);
        for (String statement : statements) {
            boolean completed = mysql.ddl(statement);
            if (!completed) {
                break;
            } else {
                log.info("Executed " + statement);
            }
        }
    }

    public void createTable() throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(MySQLTable.class));

        for (BeanDefinition bd : scanner.findCandidateComponents("com.alphaentropy.domain")) {
            Class clz = Class.forName(bd.getBeanClassName());
            Object noRecreate = clz.getAnnotation(NoRecreate.class);
            Object deprecated = clz.getAnnotation(Deprecated.class);
            if (noRecreate != null || deprecated != null) {
                continue;
            }
            List<String> statements = MySQLAnnotationUtil.createTableStatements(clz, null, true);
            for (String statement : statements) {
                boolean completed = mysql.ddl(statement);
                if (!completed) {
                    break;
                } else {
                    log.info("Executed " + statement);
                }
            }
        }
    }
}
