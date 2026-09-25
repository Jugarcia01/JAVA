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
├─ docker-compose.yml
├─ gradle/
├─ cashapp-boot/                # Módulo de arranque (Spring Boot). Entry point.
│  ├─ build.gradle
│  └─ src/main/
│     ├─ java/com/cashapp/TradingApplication.java
│     └─ resources/             # application.yaml, scripts, dbScripts
├─ common-domain/               # Modelos de dominio compartidos (records) entre módulos
│  ├─ build.gradle
│  └─ src/main/java/com/cashapp/trading/domain/model/
├─ indicator-engine/            # Agregadores/indicadores (ej. TickToBarAggregator)
├─ strategy-engine/             # Caso de uso de estrategias + adapters inbound (web/camel)
├─ market-data/                 # Adapter outbound de MarketDataPort (stub/proveedor)
├─ state-store/                 # Adapter outbound DB (R2DBC) para auditoría/estado
├─ execution-engine/            # Adapter outbound de publicación de señales
├─ common-protos/               # Contratos de comunicación es decir, Protos base (gRPC) para contratos entre módulos
└─ common-security/             # Seguridad, credenciales y sesión IBKR y/o otras plataformas.

```

## Architecture Documentation

- [Common Domain](./common-domain.md) — Shared domain records and domain primitives used across CashApp services.


## Build & run
- **Build**
  - `./gradlew build` (Linux/macOS)
  - `gradlew.bat build` (Windows)
- **Run**
  - `./gradlew :cashapp-boot:bootRun` (Linux/macOS)
  - `gradlew.bat :cashapp-boot:bootRun` (Windows)


## Cómo probar rápido
### Levantar Postgres:
```bash
docker compose up -d
```

### Crear tabla audit_logs (por psql o por cliente de base de datos):
### Script de creación de tabla

El script SQL se encuentra en `cashapp-boot/src/main/resources/dbScripts/audit_logs.sql`:

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
docker exec -i cashapp-postgres-1 psql -U ${POSTGRES_USER} -d ${POSTGRES_DB} < cashapp-boot/src/main/resources/dbScripts/audit_logs.sql
```

O alternativamente con psql local:
```bash
psql -h localhost -U ${POSTGRES_USER} -d ${POSTGRES_DB} -f cashapp-boot/src/main/resources/dbScripts/audit_logs.sql
```
Correr la app:

- **Run**
  - `./gradlew :cashapp-boot:bootRun`

### Probar endpoint:
Ejecutar el siguiente curl:
```bash
curl "http://localhost:8081/signals/evaluate?symbol=NVDA&timeframe=H1" -H "X-Correlation-Id: demo-123"
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
