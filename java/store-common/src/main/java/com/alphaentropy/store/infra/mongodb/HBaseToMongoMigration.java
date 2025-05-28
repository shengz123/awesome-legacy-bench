package com.alphaentropy.store.infra.mongodb;

import com.alphaentropy.store.infra.hbase.HBaseStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class HBaseToMongoMigration {

    @Autowired
    private HBaseStore hBaseStore;

    @Autowired
    private MongoDBStore mongoDBStore;

    /**
     * Migrates data from HBase table to MongoDB collection
     * @param hbaseTable HBase table name
     * @param mongoCollection MongoDB collection name
     * @param columnFamily HBase column family
     */
    public void migrateTable(String hbaseTable, String mongoCollection, String columnFamily) {
        log.info("Starting migration from HBase table '{}' to MongoDB collection '{}'", hbaseTable, mongoCollection);
        
        try {
            Scan scan = new Scan();
            byte[] columnFamilyBytes = Bytes.toBytes(columnFamily);
            scan.addFamily(columnFamilyBytes);
            
            ResultScanner scanner = hBaseStore.scan(scan, hbaseTable);
            int count = 0;
            List<Document> documents = new ArrayList<>();
            
            for (Result result : scanner) {
                if (result.isEmpty()) {
                    continue;
                }
                
                String rowKey = Bytes.toString(result.getRow());
                Document document = new Document();
                
                if (rowKey.length() > 6) {
                    String symbol = rowKey.substring(0, 6);
                    String date = rowKey.substring(6);
                    document.append("symbol", symbol);
                    document.append("date", date);
                    document.append("_id", rowKey);
                } else {
                    document.append("_id", rowKey);
                }
                
                for (Cell cell : result.listCells()) {
                    String column = Bytes.toString(CellUtil.cloneQualifier(cell));
                    String value = Bytes.toString(CellUtil.cloneValue(cell));
                    document.append(column, value);
                }
                
                documents.add(document);
                count++;
                
                if (count % 100 == 0) {
                    mongoDBStore.insertMany(mongoCollection, documents);
                    documents.clear();
                    log.info("Migrated {} records from HBase to MongoDB", count);
                }
            }
            
            if (!documents.isEmpty()) {
                mongoDBStore.insertMany(mongoCollection, documents);
            }
            
            log.info("Migration completed. Total records migrated: {}", count);
            scanner.close();
            
        } catch (Exception e) {
            log.error("Error during migration from HBase to MongoDB", e);
        }
    }
    
    /**
     * Migrates all tables from HBase to MongoDB
     * @param tableMap Map of HBase table names to MongoDB collection names
     * @param columnFamily HBase column family
     */
    public void migrateAllTables(Map<String, String> tableMap, String columnFamily) {
        for (Map.Entry<String, String> entry : tableMap.entrySet()) {
            migrateTable(entry.getKey(), entry.getValue(), columnFamily);
        }
    }
    
    /**
     * Creates a default table mapping for migration
     * @return Map of HBase table names to MongoDB collection names
     */
    public Map<String, String> createDefaultTableMapping() {
        Map<String, String> tableMap = new HashMap<>();
        tableMap.put("stock_data", "stockData");
        tableMap.put("financial_data", "financialData");
        tableMap.put("market_data", "marketData");
        return tableMap;
    }
}
