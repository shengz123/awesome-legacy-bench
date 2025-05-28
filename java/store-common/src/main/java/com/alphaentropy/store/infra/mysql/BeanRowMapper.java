package com.alphaentropy.store.infra.mysql;

import com.alphaentropy.common.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

@Slf4j
public class BeanRowMapper<T> implements RowMapper<T> {

    private Class<T> beanClz;

    public BeanRowMapper(Class beanClz) {
        this.beanClz = beanClz;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) {
        try {
            T ret = BeanUtils.instantiateClass(this.beanClz);
            Field[] fields = beanClz.getDeclaredFields();
            int colIdx = 1;
            for (Field field : fields) {
                Class fieldType = field.getType();
                if (fieldType.equals(String.class)) {
                    Method setter = beanClz.getDeclaredMethod(BeanUtil.setterName(field.getName()), String.class);
                    setter.invoke(ret, rs.getString(colIdx));
                } else if (fieldType.equals(Date.class)) {
                    Method setter = beanClz.getDeclaredMethod(BeanUtil.setterName(field.getName()), Date.class);
                    if (rs.getDate(colIdx) != null) {
                        setter.invoke(ret, new Date(rs.getDate(colIdx).getTime()));
                    }
                } else if (fieldType.equals(BigDecimal.class)) {
                    Method setter = beanClz.getDeclaredMethod(BeanUtil.setterName(field.getName()), BigDecimal.class);
                    setter.invoke(ret, rs.getBigDecimal(colIdx));
                }
                colIdx++;
            }
            return ret;
        } catch (Exception e) {
            log.error("Failed to map {}", beanClz.getSimpleName(), e);
        }
        return null;
    }
}
