# Hello API – Test automation (Java)

Template: **JUnit 5** + **RestAssured**.

## Prerequisites

- JDK 17+
- Maven
- Hello API running (e.g. `cd ../../api && mvn spring-boot:run`)

## Run tests

```bash
mvn test
```

With custom base URL:

```bash
mvn test -DbaseUrl=http://localhost:8080
```
