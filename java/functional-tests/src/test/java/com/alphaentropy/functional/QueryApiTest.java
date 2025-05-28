package com.alphaentropy.functional;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QueryApiTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @LocalServerPort
    private int port;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("mongodb.connection.string", mongoDBContainer::getConnectionString);
        registry.add("mongodb.database.name", () -> "test");
    }

    @BeforeAll
    public static void setup() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void testQueryEndpoint() {
        String symbol = "600000";
        String startDate = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("fields", new String[]{"price", "volume"});
        queryParam.put("filter", "price > 0");
        
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(queryParam)
            .queryParam("start", startDate)
            .queryParam("end", endDate)
        .when()
            .post("/api/{symbol}", symbol)
        .then()
            .statusCode(200)
            .body("$", notNullValue())
            .body("status", equalTo("success"));
    }

    @Test
    public void testSearchEndpoint() {
        String valueDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        
        Map<String, Object> queryParam = new HashMap<>();
        queryParam.put("fields", new String[]{"symbol", "price", "volume"});
        queryParam.put("filter", "price > 100");
        
        given()
            .port(port)
            .contentType(ContentType.JSON)
            .body(queryParam)
            .queryParam("valueDate", valueDate)
        .when()
            .post("/api/search")
        .then()
            .statusCode(200)
            .body("$", notNullValue())
            .body("status", equalTo("success"));
    }
}
