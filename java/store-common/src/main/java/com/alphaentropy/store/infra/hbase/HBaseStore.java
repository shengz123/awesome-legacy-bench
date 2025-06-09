package com.alphaentropy.store.infra.hbase;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Lazy
@Slf4j
@Repository
public class HBaseStore {
    private Connection connection;

    @PostConstruct
    public void init() {
        try {
            Configuration conf = HBaseConfiguration.create();
            connection = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            log.error("Failed to create connection to HBase.", e);
        }
    }

    private Table getTable(String name) throws IOException {
        TableName tableName = TableName.valueOf(name);
        return connection.getTable(tableName);
    }

    public Result[] get(List<Get> gets, String tableName) {
        Table table = null;
        try {
            table = getTable(tableName);
            return table.get(gets);
        } catch (IOException e) {
            log.error("Failed to get results from {}", tableName, e);
        } finally {
            closeTable(table);
        }
        return null;
    }

    public Result get(Get get, String tableName) {
        Table table = null;
        try {
            table = getTable(tableName);
            return table.get(get);
        } catch (IOException e) {
            log.error("Failed to get result from {}", tableName, e);
        } finally {
            closeTable(table);
        }
        return null;
    }

    public boolean delete(Delete delete, String tableName) {
        Table table = null;
        try {
            table = getTable(tableName);
            table.delete(delete);
            return true;
        } catch (IOException e) {
            log.error("Failed to delete from {}", tableName, e);
            return false;
        } finally {
            closeTable(table);
        }
    }

    public boolean delete(List<Delete> deletes, String tableName) {
        Table table = null;
        try {
            table = getTable(tableName);
            table.delete(deletes);
            return true;
        } catch (IOException e) {
            log.error("Failed to batch delete from {}", tableName, e);
            return false;
        } finally {
            closeTable(table);
        }
    }

    public void put(Put put, String tableName) {
        Table table = null;
        try {
            table = getTable(tableName);
            table.put(put);
        } catch (IOException e) {
            log.error("Failed to put into {}", tableName, e);
        } finally {
            closeTable(table);
        }
    }

    public void put(List<Put> puts, String tableName) {
        Table table = null;
        try {
            table = getTable(tableName);
            table.put(puts);
        } catch (IOException e) {
            log.error("Failed to batch put into {}", tableName, e);
        } finally {
            closeTable(table);
        }
    }

    public ResultScanner scan(Scan scan, String tableName) {
        Table table = null;
        try {
            table = getTable(tableName);
            return table.getScanner(scan);
        } catch (IOException e) {
            log.error("Failed to scan from {}", tableName, e);
            closeTable(table); // table can't be closed in finally as result scanner would hang.
        }
        return null;
    }

    private void closeTable(Table table) {
        if (table != null) {
            try {
                table.close();
            } catch (IOException e) {
                log.error("Failed to close table.", e);
            }
        }
    }

}
