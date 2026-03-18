# CashApp
## Overview
This system is built with Gradle, Java 21, and Spring Boot (WebFlux) and gRPC (HTTP/2). 
It follows a hexagonal architecture and integrates Apache Camel for routing and orchestration. PostgreSQL is used as the persistence layer to record and audit trades, signals, and operations.

## Project structure

```text
CashApp/
├─ README.md
├─ build.gradle
├─ settings.gradle.kts
├─ gradlew
├─ gradlew.bat
├─ gradle.properties
├─ docker-compose.yml
├─ gradle/
└─ src/main/java/com/acme/trading/
├─ TradingApplication.java
├─ domain/
│   ├─ model/
│   │   ├─ TradeSignal.java
│   │   ├─ TradeRequest.java
│   │   ├─ TradeDecision.java
│   │   └─ AuditLog.java
│   └─ service/
│       └─ SignalPolicy.java
├─ application/
│   ├─ port/in/
│   │   └─ EvaluateSignalUseCase.java
│   ├─ port/out/
│   │   ├─ AuditLogRepositoryPort.java
│   │   └─ MarketDataPort.java
│   └─ usecase/
│       └─ EvaluateSignalService.java
├─ adapters/
│   ├─ in/web/
│   │   ├─ SignalHandler.java
│   │   └─ SignalRouter.java
│   └─ out/
│       ├─ db/
│       │   ├─ R2dbcAuditLogRepository.java
│       │   └─ AuditLogEntity.java
│       └─ market/
│           └─ StubMarketDataAdapter.java
└─ infrastructure/
├─ camel/
│   ├─ RoutesConfig.java
│   └─ SignalEvaluationRoute.java
└─ config/
├─ BeansConfig.java
└─ R2dbcConfig.java

```

## Build & run
- **Build**
  - `./gradlew build` (Linux/macOS)
  - `gradlew.bat build` (Windows)
- **Run**
  - `./gradlew bootRun` (Linux/macOS)
  - `gradlew.bat bootRun` (Windows)


## Cómo probar rápido
### Levantar Postgres:
```bash
docker compose up -d
```

### Crear tabla audit_logs (por psql o por cliente de base de datos):
### Script de creación de tabla

El script SQL se encuentra en `src/main/resources/dbScripts/audit_logs.sql`:

```sql
CREATE TABLE IF NOT EXISTS audit_logs (
  id          SERIAL PRIMARY KEY,
  occurred_at TIMESTAMPTZ NOT NULL,
  level       VARCHAR(16) NOT NULL,
  category    VARCHAR(64) NOT NULL,
  message     TEXT NOT NULL,
  payload     JSONB,
  correlation_id VARCHAR(64)
  );
```

### Ejecutar el script
```bash
docker exec -i cashapp-postgres-1 psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} < src/main/resources/dbScripts/audit_logs.sql
```

O alternativamente con psql local:
```bash
psql -h localhost -U ${POSTGRES_USER} -d ${POSTGRES_DB} -f src/main/resources/dbScripts/audit_logs.sql
```
Correr la app:

- **Run**
  - `./gradlew bootRun`

### Probar endpoint:
Ejecutar el siguiente curl:
```bash
curl "http://localhost:8080/signals/evaluate?symbol=NVDA&timeframe=H1" -H "X-Correlation-Id: demo-123"
```
Se deberá observar la respuesta JSON con BUY/HOLD y en Postgres un registro será almacenado en audit_logs.


## Sources / References
- **Java 21 (JDK)**
  - https://docs.oracle.com/en/java/javase/21/
- **Gradle User Manual**
  - https://docs.gradle.org/current/userguide/userguide.html
- **Spring Boot**
  - https://docs.spring.io/spring-boot/index.html
- **Spring WebFlux**
  - https://docs.spring.io/spring-framework/reference/web/webflux.html
- **Apache Camel (Spring Boot)**
  - https://camel.apache.org/camel-spring-boot/latest/
- **Spring Data R2DBC**
  - https://spring.io/projects/spring-data-r2dbc
- **R2DBC (spec)**
  - https://r2dbc.io/
- **PostgreSQL**
  - https://www.postgresql.org/docs/
