package com.alphaentropy.store.infra.hbase;

import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

@Service
public class FactorBase extends AbstractSymbolDateDAOImpl {
    @Override
    protected String getTableName() {
        return "factor";
    }

    @Override
    protected byte[] getColumnFamilyInBytes() {
        return Bytes.toBytes("f");
    }
}
