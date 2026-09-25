# Common Domain

## 1. Purpose

`common-domain` is a shared Gradle module of CashApp that contains
domain records and other minimal, framework-independent domain types
that are required by more than one CashApp service.

The module exists primarily to prevent service-to-service Gradle
dependencies and circular dependencies that can arise when a shared
domain concept remains inside a specific service module.

`common-domain` is therefore a **shared domain model module**, not a
general-purpose shared code module.

------------------------------------------------------------------------

## 2. Architectural Role

CashApp is organized into independent service modules plus shared
infrastructure modules:

``` text
cashapp/
├── common-domain/
├── common-protos/
├── common-security/
├── market-data/
├── indicator-engine/
├── state-store/
├── strategy-engine/
├── risk-management/
├── execution-engine/
└── backtesting-engine/
```

The role of `common-domain` is complementary to the other common
modules:

  -----------------------------------------------------------------------
  Module                              Responsibility
  ----------------------------------- -----------------------------------
  `common-domain`                     Shared domain records and domain
                                      primitives

  `common-protos`                     gRPC/Protobuf communication
                                      contracts

  `common-security`                   CashApp security, credentials,
                                      secrets and IBKR
                                      authentication/session
                                      infrastructure
  -----------------------------------------------------------------------

The three modules must remain conceptually separated.

------------------------------------------------------------------------

## 3. Main Objective

The primary objectives of `common-domain` are:

-   Eliminate unnecessary Gradle dependencies between service modules.
-   Prevent circular Gradle dependencies.
-   Provide a stable location for genuinely shared domain concepts.
-   Keep shared domain types independent of infrastructure and
    frameworks.
-   Allow services to evolve independently.
-   Preserve the domain model when communication is performed through
    gRPC.

For example, if both `strategy-engine` and `risk-management` require the
same domain concept, neither service should depend directly on the other
only to reuse that type.

Instead:

``` text
             common-domain
                  │
          ┌───────┴───────┐
          ▼               ▼
 strategy-engine    risk-management
```

This removes the direct service-to-service dependency.

------------------------------------------------------------------------

## 4. What Belongs in `common-domain`

Types may be placed in `common-domain` when they satisfy the following
criteria:

1.  They represent a genuine CashApp domain concept.
2.  They are required by two or more service modules.
3.  Sharing the type prevents an undesirable service dependency or
    circular dependency.
4.  They do not depend on Spring, gRPC, persistence, IBKR SDKs or other
    infrastructure.
5.  They contain no application/service orchestration logic.
6.  Their meaning is stable enough to be shared across bounded contexts.

Typical candidates include:

``` text
Value Objects
Domain Records
Domain Enums
Domain Primitives
Shared Domain Concepts
```

Examples may include:

``` text
Instrument
Price
Quantity
Money
Side
Order
Position
Candle
MarketTick
TradingSignal
```

The actual contents must be evaluated individually. A type must not be
moved into \`common-domain merely because it is convenient to import it
from multiple modules.

------------------------------------------------------------------------

## 5. What Does NOT Belong in `common-domain`

The module must not become a generic `common` or `shared` module.

The following should remain outside `common-domain`:

### Framework components

``` text
@Service
@Component
@Configuration
@Repository
@Controller
```

### Infrastructure

``` text
Database clients
Repositories implementations
Kafka clients
gRPC clients
HTTP clients
IBKR clients
External API adapters
```

### Security

``` text
JWT configuration
OAuth2 configuration
Credentials providers
Secrets management
IBKR authentication
IBKR session management
```

These belong to `common-security` or the appropriate
service/infrastructure module.

### Application logic

``` text
Use cases
Application services
Orchestration
Workflow logic
Business process coordination
```

### Communication contracts

Protobuf messages and gRPC service definitions belong to:

``` text
common-protos
```

not `common-domain`.

### Configuration

Service-specific configuration must remain in the corresponding service.

------------------------------------------------------------------------

## 6. Domain Model vs. gRPC Contract

`common-domain` and `common-protos` solve different problems.

### `common-domain`

Answers:

> What does this concept mean inside the CashApp domain?

Example:

``` java
public record Order(
    String symbol,
    Side side,
    BigDecimal quantity,
    BigDecimal price
) {}
```

### `common-protos`

Answers:

> How is this concept communicated between services?

Example:

``` proto
message OrderRequest {
    string symbol = 1;
    Side side = 2;
    double quantity = 3;
    double price = 4;
}
```

The two representations do not have to be identical.

A gRPC adapter should translate between the transport representation and
the domain representation:

``` text
gRPC / Protobuf
      │
      ▼
    Mapper
      │
      ▼
common-domain
      │
      ▼
 Domain Logic
```

This keeps the domain independent of the communication technology.

------------------------------------------------------------------------

## 7. Dependency Rules

The following dependency rules apply to `common-domain`.

### Allowed

Service modules may depend on:

``` text
common-domain
common-protos
common-security
```

when the dependency is justified.

### Not allowed

Service modules must not depend directly on another CashApp service
merely to reuse domain classes.

For example, this should be avoided:

``` gradle
implementation project(':strategy-engine')
```

inside `risk-management` merely to reuse `TradingSignal`.

Instead:

``` gradle
implementation project(':common-domain')
```

should be used if `TradingSignal` is genuinely a shared domain concept.

### Forbidden dependency direction

`common-domain` must not depend on:

``` text
market-data
indicator-engine
state-store
strategy-engine
risk-management
execution-engine
backtesting-engine
```

The dependency direction must remain toward the shared domain layer,
never from it toward a service.

------------------------------------------------------------------------

## 8. Dependency Graph

The intended dependency model is:

``` text
                         common-domain
                              ▲
                              │
          ┌───────────────────┼───────────────────┐
          │                   │                   │
          │                   │                   │
    market-data        strategy-engine      execution-engine
          │                   │                   │
          │                   │                   │
          └───────────────────┼───────────────────┘
                              │
                         Shared Types
```

Similarly, the communication contracts are centralized independently:

``` text
                         common-protos
                              ▲
                              │
       ┌──────────────┬───────┼───────┬──────────────┐
       │              │       │       │              │
   market-data    indicator  state  strategy       risk
                              │
                              │
                         execution
                              │
                         backtesting
```

`common-domain` and `common-protos` must not be treated as
interchangeable modules.

------------------------------------------------------------------------

## 9. Domain Purity

`common-domain` should remain as close as possible to plain Java.

The preferred characteristics are:

-   Java records where appropriate.
-   Immutable value objects.
-   Enums.
-   Small domain abstractions.
-   No Spring dependencies.
-   No gRPC dependencies.
-   No persistence annotations.
-   No IBKR SDK dependencies.
-   No infrastructure configuration.
-   No environment-variable access.

For example:

``` java
public record Price(BigDecimal value) {
}
```

is appropriate.

In contrast:

``` java
@Entity
public class OrderEntity {
}
```

is not appropriate.

Likewise:

``` java
@Service
public class OrderService {
}
```

does not belong here.

------------------------------------------------------------------------

## 10. Relationship with the Service Architecture

Each CashApp service owns its own business/application logic.

For example:

``` text
market-data
    └── Market Data domain/application logic

indicator-engine
    └── Indicator calculation logic

state-store
    └── State persistence and retrieval

strategy-engine
    └── Strategy evaluation

risk-management
    └── Risk rules and decisions

execution-engine
    └── Order execution and broker integration

backtesting-engine
    └── Historical simulation
```

`common-domain` only provides domain concepts that genuinely cross those
boundaries.

It must not become the location where service-specific business logic is
accumulated.

------------------------------------------------------------------------

## 11. Criteria for Adding a New Type

Before adding a type to `common-domain`, verify:

``` text
[ ] Is this a genuine domain concept?
[ ] Is it required by more than one service?
[ ] Would keeping it in one service create an undesirable dependency?
[ ] Does moving it here eliminate or prevent a dependency cycle?
[ ] Is it independent of Spring?
[ ] Is it independent of gRPC?
[ ] Is it independent of persistence?
[ ] Is it independent of IBKR?
[ ] Does it contain no application orchestration?
[ ] Is its meaning stable enough to be shared?
```

If the answer to these questions is mostly yes, `common-domain` is a
suitable candidate.

If not, the type should remain within the appropriate service.

------------------------------------------------------------------------

## 12. Avoiding the Common Module Anti-Pattern

`common-domain` must not evolve into:

``` text
common-domain/
├── utilities/
├── services/
├── configurations/
├── repositories/
├── clients/
├── mappers/
├── security/
├── ibkr/
└── random-shared-code/
```

That structure would recreate the coupling that the modularization is
intended to remove.

The desired structure is intentionally small:

``` text
common-domain/
└── src/main/java/
    └── com/cashapp/domain/
        ├── market/
        ├── trading/
        ├── order/
        ├── position/
        └── ...
```

The exact package structure may evolve as the existing CashApp records
are reviewed.

------------------------------------------------------------------------

## 13. Refactoring Strategy

During the current CashApp refactoring, existing shared records should
be migrated incrementally.

For each candidate record:

``` text
Existing Class/Record
        │
        ▼
Identify consumers
        │
        ▼
Determine domain ownership
        │
        ├── Service-specific → keep/move to service
        │
        └── Genuinely shared
                    │
                    ▼
             common-domain
```

The objective is not to move as many classes as possible into
`common-domain`.

The objective is to establish **clean dependency boundaries**.

------------------------------------------------------------------------

## 14. Relationship with IBKR

`common-domain` must remain independent of IBKR.

For example, the following is valid:

``` text
Order
Position
Instrument
```

because these are domain concepts.

But:

``` text
IbkrClient
IbkrSession
IbkrAuthenticator
IbkrOrderAdapter
```

do not belong in `common-domain`.

IBKR authentication and session infrastructure belong to:

``` text
common-security
```

while IBKR order execution belongs to:

``` text
execution-engine
```

This preserves the separation:

``` text
common-domain
      │
      │ Domain concepts
      ▼
CashApp Services
      │
      │
      ├── common-security → IBKR authentication/session
      │
      └── execution-engine → IBKR order execution
```

------------------------------------------------------------------------

## 15. Architectural Principle

The fundamental principle of `common-domain` is:

> **Share domain concepts, not implementations.**

`common-domain` exists to provide a stable and framework-independent
representation of concepts that genuinely cross CashApp service
boundaries.

It must remain small, explicit and dependency-light.

------------------------------------------------------------------------

## 16. Current Status

`common-domain` was introduced during the CashApp modularization and
refactoring process because the existing codebase contains pre-existing
coupling between components.

Its immediate purpose is to:

-   Break Gradle circular dependencies.
-   Centralize genuinely shared domain records.
-   Allow the service modules to be extracted incrementally.
-   Establish clean boundaries without prematurely rewriting all
    existing domain logic.

As the refactoring progresses, the contents of `common-domain` should be
reviewed periodically.

A type should be removed from `common-domain` when the underlying
coupling has been eliminated and the type is no longer genuinely shared.

------------------------------------------------------------------------

## 17. Target Architecture

The target CashApp architecture is:

``` text
                          CASHAPP
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
 common-domain         common-protos       common-security
        │                    │                    │
        │               gRPC contracts      Security / Secrets
        │                    │               IBKR Authentication
        │                    │                    │
        └────────────────────┼────────────────────┘
                             │
                             ▼
                    ┌─────────────────┐
                    │ CashApp Services │
                    └────────┬────────┘
                             │
       ┌─────────────┬───────┼────────┬──────────────┐
       │             │       │        │              │
       ▼             ▼       ▼        ▼              ▼
 Market Data    Indicator  State   Strategy       Risk
                Engine     Store   Engine         Management
                                                   │
                                                   ▼
                                            Execution Engine
                                                   │
                                                   ▼
                                                  IBKR

                             ┌──────────────────────┐
                             │ Backtesting Engine   │
                             └──────────────────────┘
```

`common-domain` is therefore a **supporting architectural module**, not
a business service and not a replacement for the service boundaries.

Its success is measured by whether it helps maintain those boundaries
while keeping the shared domain model clean and dependency-free.
