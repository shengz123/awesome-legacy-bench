package com.alphaentropy.store.infra.mongodb;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Lazy
@Slf4j
@Repository
public class MongoDBStore {
    private MongoClient mongoClient;
    private MongoDatabase database;

    @Value("${mongodb.connection.string:mongodb://localhost:27017}")
    private String connectionString;

    @Value("${mongodb.database.name:alphaentropy}")
    private String databaseName;

    @PostConstruct
    public void init() {
        try {
            mongoClient = MongoClients.create(connectionString);
            database = mongoClient.getDatabase(databaseName);
            log.info("Successfully connected to MongoDB database: {}", databaseName);
        } catch (Exception e) {
            log.error("Failed to create connection to MongoDB.", e);
        }
    }

    private MongoCollection<Document> getCollection(String name) {
        return database.getCollection(name);
    }

    public Document findOne(String collectionName, Bson filter) {
        try {
            return getCollection(collectionName).find(filter).first();
        } catch (Exception e) {
            log.error("Failed to find document in collection: {}", collectionName, e);
            return null;
        }
    }

    public List<Document> find(String collectionName, Bson filter) {
        List<Document> results = new ArrayList<>();
        try {
            FindIterable<Document> documents = getCollection(collectionName).find(filter);
            for (Document document : documents) {
                results.add(document);
            }
        } catch (Exception e) {
            log.error("Failed to find documents in collection: {}", collectionName, e);
        }
        return results;
    }

    public void insertOne(String collectionName, Document document) {
        try {
            getCollection(collectionName).insertOne(document);
        } catch (Exception e) {
            log.error("Failed to insert document into collection: {}", collectionName, e);
        }
    }

    public void insertMany(String collectionName, List<Document> documents) {
        try {
            getCollection(collectionName).insertMany(documents);
        } catch (Exception e) {
            log.error("Failed to insert documents into collection: {}", collectionName, e);
        }
    }

    public void updateOne(String collectionName, Bson filter, Document update) {
        try {
            getCollection(collectionName).updateOne(filter, new Document("$set", update));
        } catch (Exception e) {
            log.error("Failed to update document in collection: {}", collectionName, e);
        }
    }

    public void updateMany(String collectionName, Bson filter, Document update) {
        try {
            getCollection(collectionName).updateMany(filter, new Document("$set", update));
        } catch (Exception e) {
            log.error("Failed to update documents in collection: {}", collectionName, e);
        }
    }

    public void upsert(String collectionName, Bson filter, Document update) {
        try {
            UpdateOptions options = new UpdateOptions().upsert(true);
            getCollection(collectionName).updateOne(filter, new Document("$set", update), options);
        } catch (Exception e) {
            log.error("Failed to upsert document in collection: {}", collectionName, e);
        }
    }

    public void deleteOne(String collectionName, Bson filter) {
        try {
            getCollection(collectionName).deleteOne(filter);
        } catch (Exception e) {
            log.error("Failed to delete document from collection: {}", collectionName, e);
        }
    }

    public void deleteMany(String collectionName, Bson filter) {
        try {
            getCollection(collectionName).deleteMany(filter);
        } catch (Exception e) {
            log.error("Failed to delete documents from collection: {}", collectionName, e);
        }
    }

    public Document findBySymbolAndDate(String collectionName, String symbol, String date) {
        try {
            Bson filter = Filters.and(
                Filters.eq("symbol", symbol),
                Filters.eq("date", date)
            );
            return getCollection(collectionName).find(filter).first();
        } catch (Exception e) {
            log.error("Failed to find document by symbol and date in collection: {}", collectionName, e);
            return null;
        }
    }

    public List<Document> findBySymbol(String collectionName, String symbol) {
        List<Document> results = new ArrayList<>();
        try {
            Bson filter = Filters.eq("symbol", symbol);
            FindIterable<Document> documents = getCollection(collectionName).find(filter);
            for (Document document : documents) {
                results.add(document);
            }
        } catch (Exception e) {
            log.error("Failed to find documents by symbol in collection: {}", collectionName, e);
        }
        return results;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}
