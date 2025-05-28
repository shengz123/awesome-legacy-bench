package com.alphaentropy.store.infra.hbase;

import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.stereotype.Service;

@Service
public class QuarterFactorBase extends AbstractSymbolDateDAOImpl {
    @Override
    protected String getTableName() {
        return "factor_q";
    }

    @Override
    protected byte[] getColumnFamilyInBytes() {
        return Bytes.toBytes("fq");
    }
}
