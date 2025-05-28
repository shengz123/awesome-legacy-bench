package com.alphaentropy.store.application;

import com.alphaentropy.common.utils.MySQLAnnotationUtil;
import com.alphaentropy.domain.annotation.MySQLVarchar;
import com.alphaentropy.domain.common.SymbolDateKey;
import com.alphaentropy.store.infra.mysql.BeanRowMapper;
import com.alphaentropy.store.infra.mysql.MySQLConnFactory;
import com.alphaentropy.store.infra.mysql.SymbolDateRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alphaentropy.common.utils.MySQLAnnotationUtil.DEFAULT_VARCHAR_LEN;

@Service
@Slf4j
public class MySQLStatement {
    @Autowired
    private MySQLConnFactory connFactory;

    public boolean ddl(String statement) {
        try {
            connFactory.getJdbcTemplate().update(statement);
            return true;
        } catch (Exception e) {
            log.error("Failed to create table with " + statement, e);
        }
        return false;
    }

    public Object pointQuery(String statement, RowMapper rowMapper) {
        List list = connFactory.getJdbcTemplate().query(statement, rowMapper);
        if (list != null && !list.isEmpty()) {
            list.get(0);
        }
        return null;
    }

    public List queryAll(Class clz) {
        String querySQL = MySQLAnnotationUtil.queryAll(clz);
        return connFactory.getJdbcTemplate().query(querySQL, new BeanRowMapper<>(clz));
    }

    public List queryPast(Class clz, String symbol, Date date, int pastDays) {
        String querySQL = MySQLAnnotationUtil.queryPast(clz);
        return connFactory.getJdbcTemplate().query(querySQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, symbol);
                ps.setDate(2, new java.sql.Date(date.getTime()));
                ps.setInt(3, pastDays);
            }
        }, new BeanRowMapper(clz));
    }

    public List queryByDate(Class clz, Date date) {
        String querySQL = MySQLAnnotationUtil.queryByDate(clz);
        return connFactory.getJdbcTemplate().query(querySQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setDate(1, new java.sql.Date(date.getTime()));
            }
        }, new BeanRowMapper(clz));
    }

    public List queryBySymbol(Class clz, String symbol, boolean ascending) {
        String querySQL = MySQLAnnotationUtil.queryBySymbol(clz, ascending);
        return connFactory.getJdbcTemplate().query(querySQL, new BeanRowMapper(clz), symbol);
    }

    public Object queryBySymbolStatic(Class clz, String symbol, String additionalCond) {
        String querySQL = MySQLAnnotationUtil.queryBySymbolStatic(clz) + additionalCond;
        List list = connFactory.getJdbcTemplate().query(querySQL, new BeanRowMapper(clz), symbol);
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }

    public Object queryBySymbolDate(Class clz, String symbol, Date date) {
        String querySQL = MySQLAnnotationUtil.queryBySymbolDate(clz);
        List ret = connFactory.getJdbcTemplate().query(querySQL, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, symbol);
                ps.setDate(2, new java.sql.Date(date.getTime()));
            }
        }, new BeanRowMapper<>(clz));
        if (ret == null || ret.isEmpty()) {
            return null;
        }
        return ret.get(0);
    }

    public boolean deleteTable(Class clz) {
        String deleteSQL = MySQLAnnotationUtil.deleteFromTable(clz);
        try {
            connFactory.getJdbcTemplate().execute(deleteSQL);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete table with " + deleteSQL, e);
        }
        return false;
    }

    public boolean deleteBySymbol(Class clz, String symbol) {
        String deleteSQL = MySQLAnnotationUtil.deleteBySymbol(clz);
        try {
            connFactory.getJdbcTemplate().update(deleteSQL, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    ps.setString(1, symbol);
                }
            });
            return true;
        } catch (Exception e) {
            log.error("Failed to delete row with " + deleteSQL, e);
        }
        return false;
    }

    public Date getLatest(Class clz) {
        String statement = MySQLAnnotationUtil.getLatest(clz);
        java.sql.Date latest = connFactory.getJdbcTemplate().queryForObject(statement, java.sql.Date.class);
        if (latest != null) {
            return new Date(latest.getTime());
        }
        return null;
    }

    public Map<String, Date> getMostRecent(Class clz) {
        Map<String, Date> ret = new HashMap<>();
        String statement = MySQLAnnotationUtil.getMostRecentStatement(clz);
        List<SymbolDateKey> list = connFactory.getJdbcTemplate().query(statement, new SymbolDateRowMapper());
        for (SymbolDateKey key : list) {
            ret.put(key.getSymbol(), key.getDate());
        }
        return ret;
    }

    public List<SymbolDateKey> getSymbolDates(Class clz) {
        String statement = MySQLAnnotationUtil.querySymbolDate(clz);
        return connFactory.getJdbcTemplate().query(statement, new SymbolDateRowMapper());
    }

    public boolean insert(Object data, Class clz) {
        String insertion = MySQLAnnotationUtil.insertStatement(clz);
        try {
            connFactory.getJdbcTemplate().update(insertion, new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    Field[] fields = clz.getDeclaredFields();
                    int psIndex = 1;
                    for (Field field : fields) {
                        setPreparedStatement(psIndex, ps, field, data);
                        psIndex++;
                    }
                }
            });
            return true;
        } catch (Exception e) {
            log.error("Failed to insert " + data, e);
        }
        return false;
    }

    public boolean batchInsert(List list, Class clz) {
        String insertion = MySQLAnnotationUtil.insertStatement(clz);
        try {
            connFactory.getJdbcTemplate().batchUpdate(insertion, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Object data = list.get(i);
                    Field[] fields = clz.getDeclaredFields();
                    int psIndex = 1;
                    for (Field field : fields) {
                        setPreparedStatement(psIndex, ps, field, data);
                        psIndex++;
                    }
                }

                @Override
                public int getBatchSize() {
                    return list.size();
                }
            });
            return true;
        } catch (Exception e) {
            log.error("Failed to batch insert " + clz, e);
        }
        return false;
    }

    private void setPreparedStatement(int psIndex, PreparedStatement ps, Field field, Object data) {
        try {
            field.setAccessible(true);
            Object fieldVal = field.get(data);
            if (field.getType().equals(String.class)) {
                if (fieldVal == null) {
                    ps.setNull(psIndex, Types.VARCHAR);
                } else {
                    MySQLVarchar varchar = field.getAnnotation(MySQLVarchar.class);
                    String fieldStringVal = (String) fieldVal;
                    int newLen = Math.min(fieldStringVal.length(), Integer.valueOf(DEFAULT_VARCHAR_LEN) - 1);
                    if (varchar != null) {
                        newLen = Math.min(fieldStringVal.length(), Integer.valueOf(varchar.value()) - 1);
                    }
                    ps.setString(psIndex, fieldStringVal.substring(0, newLen));
                }
            } else if (field.getType().equals(Date.class)) {
                if (fieldVal == null) {
                    ps.setNull(psIndex, Types.DATE);
                } else {
                    ps.setDate(psIndex, convertDate((java.util.Date) fieldVal));
                }
            } else if (field.getType().equals(BigDecimal.class)) {
                if (fieldVal == null) {
                    ps.setNull(psIndex, Types.NUMERIC);
                } else {
                    ps.setBigDecimal(psIndex, (BigDecimal) fieldVal);
                }
            }
        } catch (Exception e) {
            log.error("Failed to set the statement", e);
        }
    }

    private java.sql.Date convertDate(java.util.Date date) {
        return  new java.sql.Date(date.getTime());
    }

    private java.util.Date convertDate(java.sql.Date date) {
        return  new java.util.Date(date.getTime());
    }

}
