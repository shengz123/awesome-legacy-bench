package com.alphaentropy.store.infra.mysql;

import com.alphaentropy.domain.common.SymbolDateKey;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SymbolDateRowMapper implements RowMapper<SymbolDateKey> {
    @Override
    public SymbolDateKey mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SymbolDateKey(rs.getString(1), new Date(rs.getDate(2).getTime()));
    }
}
