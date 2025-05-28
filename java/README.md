# Alphaentropy Core Project

A comprehensive financial data processing and analysis platform.

## Project Overview

This project provides a suite of tools for processing, analyzing, and querying financial data. It has been modernized to use Java 17, Spring Boot 3, and MongoDB, with a microservice architecture.

## Completed Tasks

- ✅ **Removed factor and factor-core modules** while preserving Factor annotation in domain
- ✅ **Upgraded Java version from 8 to 17** and updated all dependencies
- ✅ **Adopted microservice architecture** with Spring Boot 3.2.0
- ✅ **Created MongoDB integration** to replace HBase
- ✅ **Integrated with LLM** using Spring AI
- ✅ **Generated functional tests** with RestAssured
- ✅ **Created containerization** with Docker and Docker Compose
- ✅ **Added comprehensive documentation**

## Modules

- **domain**: Core domain models and annotations
- **common-utils**: Shared utility classes
- **store-common**: Data access layer with MongoDB support
- **microservice**: REST API endpoints with Spring Boot
- **functional-tests**: Integration tests using RestAssured
- **web-loader**: Web data loading utilities
- **tdx-loader**: TDX data loading utilities

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- MongoDB 5.0 or higher
- Docker and Docker Compose (optional)

### Building the Project

```bash
mvn clean install
```

### Running the Microservice

```bash
cd microservice
mvn spring-boot:run
```

### Running with Docker

```bash
docker-compose up -d
```

## API Documentation

The REST API provides the following endpoints:

- `POST /api/{symbol}`: Query data for a specific symbol
- `POST /api/search`: Search for data based on criteria

## Migration from HBase to MongoDB

The project has been migrated from HBase to MongoDB. To migrate your existing data:

1. Ensure both HBase and MongoDB are running
2. Run the migration utility:

```java
@Autowired
private HBaseToMongoMigration migration;

// In your application
migration.migrateAllTables(migration.createDefaultTableMapping(), "cf");
```

## LLM Integration

The project integrates with OpenAI's GPT models using Spring AI. To use this feature:

1. Set your OpenAI API key in the environment variable `OPENAI_API_KEY`
2. Use the `LLMService` to generate responses or analyze financial data

## Testing

### Unit Tests

```bash
mvn test
```

### Functional Tests

```bash
cd functional-tests
mvn test
```

## Contributing

Please follow the standard Git workflow:

1. Create a feature branch
2. Make your changes
3. Run tests
4. Submit a pull request

