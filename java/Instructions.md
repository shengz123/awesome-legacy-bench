# Awesome Legacy Bench for Java

A benchmark of tasks for legacy system transformation in Java project.

## Tasks

- **Remove unused modules - factor and factor-core**: Remove the factor module from the project
  - Keep Factor in domain module
  - Remove the module factor and the usages in other modules

- **Generate unit tests**: Create unit tests for the existing codebase

- **Java version upgrade**: Upgrade Java version from 1.8 to 17
  - Upgrade all the relevant dependencies to compatible versions

- **Adopt microservice**: Use Spring Boot to build a microservice

- **Generate functional tests on restful api**: Create a new module for functional tests
  - Use RestAssured to write tests for restful api

- **Migrate Hbase to Mango DB**: Use MangoDB as the database and migrate data from HBase
  - Use MangoDB driver to connect to MangoDB
  - Use MangoDB migration tool to migrate data from HBase

- **Integrate with LLM**: Allow LLM to call the restful api
  - Use Spring AI to integrate with LLM

- **Generate containerization dockerfile**: Generate the dockerfile for the microservice

- **Write Documentation**: Document the project

