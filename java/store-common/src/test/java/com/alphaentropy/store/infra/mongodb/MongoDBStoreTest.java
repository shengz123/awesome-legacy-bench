package com.alphaentropy.store.infra.mongodb;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MongoDBStoreTest {

    @Mock
    private MongoClient mongoClient;

    @Mock
    private MongoDatabase database;

    @Mock
    private MongoCollection<Document> collection;

    @Mock
    private FindIterable<Document> findIterable;

    @Mock
    private InsertOneResult insertOneResult;

    @Mock
    private InsertManyResult insertManyResult;

    @Mock
    private UpdateResult updateResult;

    @Mock
    private DeleteResult deleteResult;

    @InjectMocks
    private MongoDBStore mongoDBStore;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(mongoDBStore, "connectionString", "mongodb://localhost:27017");
        ReflectionTestUtils.setField(mongoDBStore, "databaseName", "testdb");
        ReflectionTestUtils.setField(mongoDBStore, "database", database);
        
        when(database.getCollection(anyString())).thenReturn(collection);
    }

    @Test
    public void testFindOne() {
        Document expectedDocument = new Document("_id", "123").append("name", "test");
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(expectedDocument);

        Document result = mongoDBStore.findOne("testCollection", new Document("_id", "123"));

        assertNotNull(result);
        assertEquals("123", result.getString("_id"));
        assertEquals("test", result.getString("name"));
        verify(collection).find(any(Document.class));
    }

    @Test
    public void testFind() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("_id", "1").append("name", "test1"));
        documents.add(new Document("_id", "2").append("name", "test2"));
        
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(documents.iterator());

        List<Document> results = mongoDBStore.find("testCollection", new Document("name", "test"));

        assertNotNull(results);
        assertEquals(2, results.size());
        assertEquals("test1", results.get(0).getString("name"));
        assertEquals("test2", results.get(1).getString("name"));
        verify(collection).find(any(Document.class));
    }

    @Test
    public void testInsertOne() {
        Document document = new Document("_id", "123").append("name", "test");
        when(collection.insertOne(any(Document.class))).thenReturn(insertOneResult);

        mongoDBStore.insertOne("testCollection", document);

        verify(collection).insertOne(document);
    }

    @Test
    public void testInsertMany() {
        List<Document> documents = new ArrayList<>();
        documents.add(new Document("_id", "1").append("name", "test1"));
        documents.add(new Document("_id", "2").append("name", "test2"));
        
        when(collection.insertMany(anyList())).thenReturn(insertManyResult);

        mongoDBStore.insertMany("testCollection", documents);

        verify(collection).insertMany(documents);
    }

    @Test
    public void testUpdateOne() {
        Document filter = new Document("_id", "123");
        Document update = new Document("name", "updated");
        when(collection.updateOne(any(Document.class), any(Document.class))).thenReturn(updateResult);

        mongoDBStore.updateOne("testCollection", filter, update);

        verify(collection).updateOne(eq(filter), any(Document.class));
    }

    @Test
    public void testDeleteOne() {
        Document filter = new Document("_id", "123");
        when(collection.deleteOne(any(Document.class))).thenReturn(deleteResult);

        mongoDBStore.deleteOne("testCollection", filter);

        verify(collection).deleteOne(filter);
    }

    @Test
    public void testFindBySymbolAndDate() {
        Document expectedDocument = new Document("symbol", "AAPL").append("date", "20220101");
        when(collection.find(any(Document.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(expectedDocument);

        Document result = mongoDBStore.findBySymbolAndDate("testCollection", "AAPL", "20220101");

        assertNotNull(result);
        assertEquals("AAPL", result.getString("symbol"));
        assertEquals("20220101", result.getString("date"));
        verify(collection).find(any(Document.class));
    }
}
