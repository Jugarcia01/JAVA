# Common Security

## 1. Purpose

`common-security` is a shared Gradle infrastructure module of CashApp
responsible for centralizing security-related capabilities that must be
reused across CashApp services.

Its purpose is to prevent duplication of security infrastructure,
credentials handling and IBKR authentication/session management while
keeping business logic and service-specific authorization policies
inside the appropriate service modules.

The module provides reusable security infrastructure; it does not
contain CashApp business logic.

------------------------------------------------------------------------

## 2. Architectural Role

CashApp contains three shared modules with clearly separated
responsibilities:

  -----------------------------------------------------------------------
  Module                              Responsibility
  ----------------------------------- -----------------------------------
  `common-domain`                     Shared domain records and domain
                                      primitives

  `common-protos`                     gRPC/Protobuf communication
                                      contracts

  `common-security`                   Security infrastructure,
                                      credentials, secrets and IBKR
                                      authentication/session management
  -----------------------------------------------------------------------

The distinction is important:

``` text
common-domain
    → What the domain concepts are

common-protos
    → How services communicate

common-security
    → How access and protected external connections are secured
```

------------------------------------------------------------------------

## 3. Main Objectives

The objectives of `common-security` are:

-   Centralize shared CashApp security infrastructure.
-   Avoid duplicating OAuth2/JWT configuration across services.
-   Provide a common reactive JWT decoder.
-   Provide shared security configuration that can be reused by
    services.
-   Centralize secure credentials retrieval.
-   Resolve protected environment variables/secrets without exposing
    them to business logic.
-   Encapsulate IBKR authentication.
-   Encapsulate IBKR session establishment and lifecycle management.
-   Prevent IBKR authentication code from being embedded in a specific
    CashApp business service.
-   Provide stable security abstractions that can be consumed by
    services independently.

------------------------------------------------------------------------

## 4. CashApp Application Security

CashApp services expose protected endpoints and must authenticate
incoming requests.

`common-security` provides the shared infrastructure required for this
purpose.

The intended model is:

``` text
Client
   │
   │ JWT
   ▼
CashApp Service
   │
   ▼
Shared Security Infrastructure
   │
   ├── OAuth2 Resource Server
   ├── Reactive JWT Decoder
   └── Authentication
   │
   ▼
Service-specific Authorization
   │
   └── @PreAuthorize
```

The service remains responsible for its own authorization rules.

For example:

``` java
@PreAuthorize("hasAuthority('SCOPE_execution')")
public Mono<OrderResponse> executeOrder(...) {
    ...
}
```

The shared module provides the infrastructure; the service defines which
authenticated users or scopes are allowed to invoke a particular
operation.

------------------------------------------------------------------------

## 5. Shared Security Configuration

The module provides shared security components such as:

``` text
SharedSecurityConfig
JwtDecoderConfig
SecurityProperties
```

A service can use the shared infrastructure while retaining its own
service-specific `SecurityConfig`.

Conceptually:

``` text
common-security
        │
        ▼
SharedSecurityConfig
        │
        ├── JWT decoder
        ├── OAuth2 Resource Server
        └── shared security infrastructure
                     │
                     ▼
             Service SecurityConfig
                     │
                     ├── Endpoint rules
                     ├── Public endpoints
                     └── @PreAuthorize
```

This prevents every service from implementing its own JWT infrastructure
independently.

------------------------------------------------------------------------

## 6. Authentication vs. Authorization

CashApp must maintain a clear distinction between authentication and
authorization.

### Authentication

Answers:

> Who or what is making the request?

Examples:

``` text
JWT validation
Token decoding
Credential validation
IBKR authentication
IBKR session establishment
```

### Authorization

Answers:

> Is this authenticated principal allowed to perform this operation?

Examples:

``` text
@PreAuthorize(...)
Scopes
Roles
Service permissions
Endpoint access rules
```

`common-security` provides shared authentication infrastructure.

Each service remains responsible for its business-specific authorization
policies.

------------------------------------------------------------------------

## 7. Credentials and Secrets

Credentials must not be embedded directly into CashApp source code.

The module should provide abstractions for retrieving protected
credentials.

Conceptually:

``` text
Environment / Secret Store
          │
          ▼
   Secret Provider
          │
          ▼
 Credentials Provider
          │
          ▼
     Domain-neutral
    security object
```

For example:

``` java
public interface CredentialsProvider {

    Credentials getCredentials(String name);
}
```

The concrete implementation may initially use environment variables or
another configured mechanism and may later be replaced by a dedicated
secret-management platform.

Potential future providers include:

``` text
AWS Secrets Manager
Azure Key Vault
HashiCorp Vault
Kubernetes Secrets
Environment Variables
```

The consuming service should not need to change when the underlying
secret provider changes.

------------------------------------------------------------------------

## 8. Protected Environment Variables

Where environment variables are used, access to sensitive values must be
centralized.

Application code should not contain repeated code such as:

``` java
System.getenv("IBKR_USERNAME");
System.getenv("IBKR_PASSWORD");
System.getenv("IBKR_TOKEN");
```

distributed across services.

Instead, services should consume an abstraction supplied by
`common-security`.

For example:

``` text
Service
   │
   ▼
CredentialsProvider
   │
   ▼
SecretProvider
   │
   ▼
Environment / Secret Store
```

This provides a single point where secret resolution, validation and
future encryption/secret-store integration can be implemented.

------------------------------------------------------------------------

## 9. IBKR Security Boundary

`common-security` is also responsible for the security infrastructure
required to access the Interactive Brokers API.

This includes:

``` text
IBKR credentials
IBKR authentication
IBKR session establishment
IBKR session lifecycle
IBKR security configuration
```

The objective is explicitly to prevent this infrastructure from being
embedded inside a CashApp business module.

The intended architecture is:

``` text
                  common-security
                        │
                        ▼
                 IBKR Security
                        │
             ┌──────────┼──────────┐
             │          │          │
             ▼          ▼          ▼
        Credentials Authentication Session
             │          │          │
             └──────────┴──────────┘
                        │
                        ▼
                       IBKR
```

------------------------------------------------------------------------

## 10. IBKR Authentication

IBKR authentication is an infrastructure concern.

It should therefore be encapsulated behind an abstraction such as:

``` java
public interface IbkrAuthenticator {

    IbkrAuthentication authenticate(
        IbkrCredentials credentials
    );
}
```

The concrete implementation handles the details of the IBKR
authentication mechanism.

CashApp business services must not implement the authentication protocol
themselves.

------------------------------------------------------------------------

## 11. IBKR Session Management

After authentication, the module is responsible for managing the
resulting IBKR session.

Conceptually:

``` java
public interface IbkrSession {

    void connect();

    boolean isConnected();

    SessionStatus status();

    void disconnect();
}
```

The actual interface should reflect the requirements of the IBKR API
integration being used.

The important architectural principle is that the lifecycle of the
external authenticated session is isolated from CashApp business logic.

------------------------------------------------------------------------

## 12. Relationship with Execution Engine

`common-security` does not execute trading operations.

This distinction is fundamental.

### `common-security`

Responsible for:

``` text
Credentials
Authentication
Session
Security
```

### `execution-engine`

Responsible for:

``` text
Create order
Submit order
Cancel order
Track order
Process fills
Manage execution state
Interact with broker through an adapter
```

The relationship is:

``` text
                 Execution Engine
                        │
                        ▼
                 Broker Gateway
                        │
                        ▼
                IbkrBrokerAdapter
                        │
                        │ uses
                        ▼
                 common-security
                        │
                  IbkrSession
                        │
                        ▼
                       IBKR
```

Therefore:

> `execution-engine` knows how to execute trading operations;
> `common-security` knows how to establish and protect the authenticated
> connection required to access IBKR.

------------------------------------------------------------------------

## 13. IBKR Adapter Boundary

The execution module should isolate IBKR-specific trading operations
behind an adapter.

For example:

``` text
execution-engine
│
├── application/
│
├── domain/
│
├── infrastructure/
│   └── ibkr/
│       └── IbkrBrokerAdapter
│
└── interfaces/
```

The adapter may consume security infrastructure from `common-security`:

``` text
IbkrBrokerAdapter
       │
       ├── IbkrSession
       ├── IbkrCredentials / security abstractions
       └── IBKR API
```

This means the execution engine does not implement the authentication
mechanism.

------------------------------------------------------------------------

## 14. Security and IBKR Are Related but Separate Concerns

`common-security` intentionally contains both CashApp security
infrastructure and IBKR security infrastructure because both are
cross-cutting security concerns.

However, they must remain internally separated:

``` text
common-security/
│
├── cashapp/
│   ├── SharedSecurityConfig
│   ├── JwtDecoderConfig
│   └── SecurityProperties
│
├── credentials/
│   ├── Credentials
│   └── CredentialsProvider
│
├── secrets/
│   ├── SecretProvider
│   └── SecretResolver
│
└── ibkr/
    ├── authentication/
    │   ├── IbkrAuthenticator
    │   └── IbkrAuthentication
    │
    ├── session/
    │   ├── IbkrSession
    │   └── IbkrSessionManager
    │
    └── configuration/
        └── IbkrProperties
```

The module therefore remains coherent without mixing business
responsibilities into the shared security layer.

------------------------------------------------------------------------

## 15. What Does NOT Belong in `common-security`

The following must remain outside this module:

### Trading logic

``` text
Buy
Sell
Entry
Exit
Signal generation
Strategy rules
Position sizing
Risk decisions
```

These belong to the appropriate domain service.

### Order execution

``` text
Create order
Cancel order
Process fill
Execution strategy
```

These belong to `execution-engine`.

### Market data processing

``` text
Tick processing
Candle aggregation
Market data normalization
```

These belong to `market-data`.

### Indicator calculation

``` text
SMA
EMA
MMA
RSI
MACD
Cycle calculations
```

These belong to `indicator-engine`.

### Domain records

Shared domain records belong to:

``` text
common-domain
```

unless they are strictly security-specific.

### gRPC contracts

Protobuf definitions belong to:

``` text
common-protos
```

------------------------------------------------------------------------

## 16. Dependency Rules

The following dependency rules apply to `common-security`.

### Allowed

CashApp services may depend on:

``` text
common-security
```

when they require shared security infrastructure.

### Not allowed

`common-security` must not depend on CashApp service modules.

For example:

``` text
common-security
      X
      │
      └── execution-engine
```

is forbidden.

The correct direction is:

``` text
execution-engine
      │
      ▼
common-security
```

### Domain independence

`common-security` should not introduce security dependencies into
`common-domain`.

The dependency direction must remain:

``` text
common-domain
      │
      X
      │
common-security
```

not the reverse.

------------------------------------------------------------------------

## 17. Dependency Graph

The intended Gradle dependency direction is:

``` text
                         common-security
                               ▲
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              │                │                │
       market-data       execution-engine   other services
                               │
                               ▼
                         IBKR Adapter
```

`common-security` remains below the service layer from a dependency
perspective.

It provides infrastructure that services consume.

------------------------------------------------------------------------

## 18. Security Configuration per Service

Each service may have its own `SecurityConfig`.

The shared module provides reusable infrastructure, while the service
configures its own authorization policy.

Example:

``` java
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/actuator/health").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(Customizer.withDefaults())
            )
            .build();
    }
}
```

Business authorization can then be expressed using:

``` java
@PreAuthorize("hasAuthority('SCOPE_execution')")
```

The exact roles/scopes must be defined according to the CashApp security
model.

------------------------------------------------------------------------

## 19. Security Configuration Must Not Leak into Domain

The domain layer must remain independent of security infrastructure.

For example, this is undesirable:

``` java
public record TradingSignal(
    Authentication authentication,
    String symbol
) {}
```

A domain object should not know about:

``` text
Spring Security
JWT
OAuth2
HTTP
gRPC security
IBKR sessions
```

Security context belongs at the application/interface/infrastructure
boundaries.

------------------------------------------------------------------------

## 20. Error Handling and Security Failures

Security failures must remain distinguishable from business failures.

Examples:

``` text
Authentication failure
Authorization failure
Credential resolution failure
IBKR authentication failure
IBKR session failure
Business validation failure
Risk rejection
Order rejection
```

They must not all be represented as generic exceptions.

This distinction becomes particularly important when the Execution
Engine communicates with Risk Management and IBKR.

------------------------------------------------------------------------

## 21. Observability Considerations

Security-related operations should be observable without exposing
secrets.

Logs may contain:

``` text
Authentication attempt
Authentication success/failure
Session established/lost
Credential provider failure
IBKR connection state
```

Logs must never contain:

``` text
Passwords
Tokens
Private keys
Secrets
Authorization headers
Sensitive credential values
```

Sensitive values must be redacted if they could accidentally appear in
diagnostic output.

------------------------------------------------------------------------

## 22. Testing Strategy

`common-security` should be tested independently from the business
services.

Tests should cover at least:

``` text
JWT validation
JWT decoder configuration
Authorization configuration
Credential resolution
Missing credentials
Invalid credentials
Secret resolution
IBKR authentication
IBKR session lifecycle
Session expiration/reconnection where applicable
Security failure handling
```

External IBKR connectivity should not be required for the majority of
unit tests.

Use mocks, test doubles or controlled integration tests for external
dependencies.

------------------------------------------------------------------------

## 23. Refactoring Strategy

During the current CashApp modularization, existing security-related
code should be migrated incrementally.

For each existing security component:

``` text
Existing component
        │
        ▼
Classify responsibility
        │
        ├── CashApp security infrastructure
        │       └── common-security
        │
        ├── IBKR authentication/session
        │       └── common-security
        │
        ├── Trading execution
        │       └── execution-engine
        │
        └── Service-specific authorization
                └── corresponding service
```

The objective is not simply to move classes.

The objective is to establish a clear security boundary that can be
reused by all services.

------------------------------------------------------------------------

## 24. Criteria for Adding a New Component

Before adding a component to `common-security`, verify:

``` text
[ ] Is it a security/infrastructure concern?
[ ] Is it shared by two or more services, or intentionally centralized?
[ ] Does centralization reduce duplication?
[ ] Is it independent of business logic?
[ ] Does it avoid dependency on a specific service?
[ ] Does it avoid exposing secrets?
[ ] Does it have a stable abstraction?
[ ] Is it independent of domain-specific strategy/risk logic?
```

If the component performs business operations, it should not be added to
`common-security`.

------------------------------------------------------------------------

## 25. Target Architecture

The target CashApp architecture is:

``` text
                              CASHAPP
                                 │
       ┌─────────────────────────┼─────────────────────────┐
       │                         │                         │
       ▼                         ▼                         ▼
 common-domain             common-protos            common-security
       │                         │                         │
 Domain Records             gRPC Contracts       ┌─────────┴─────────┐
                                                  │                   │
                                            CashApp Security      IBKR Security
                                                  │                   │
                                             JWT/OAuth2         Credentials
                                             Authorization       Authentication
                                                                  Session
       │                         │                         │
       └─────────────────────────┼─────────────────────────┘
                                 │
                                 ▼
                        CashApp Service Layer
                                 │
          ┌──────────────┬───────┼────────┬──────────────┐
          │              │       │        │              │
          ▼              ▼       ▼        ▼              ▼
      Market Data   Indicator   State   Strategy       Risk
                   Engine       Store   Engine       Management
                                                        │
                                                        ▼
                                                 Execution Engine
                                                        │
                                                        ▼
                                                IBKR Broker Adapter
                                                        │
                                                        ▼
                                                       IBKR
```

------------------------------------------------------------------------

## 26. Architectural Principle

The fundamental principle of `common-security` is:

> **Centralize security infrastructure, not business logic.**

The module should provide the reusable security foundation required by
CashApp while keeping authorization policies, trading operations and
business rules within their respective services.

For IBKR specifically:

> **`common-security` owns credentials, authentication and session
> infrastructure; `execution-engine` owns trading operations and broker
> interaction through an adapter.**

This separation allows the CashApp services to remain independently
deployable and prevents authentication details from becoming embedded in
business-specific modules.

------------------------------------------------------------------------

## 27. Current Status

`common-security` is part of the CashApp modularization architecture.

Its responsibilities include:

``` text
CashApp security infrastructure
        +
Credentials and secrets
        +
IBKR authentication
        +
IBKR session management
```

The module should be implemented incrementally as the existing CashApp
security and IBKR integration code is extracted from the current
codebase.

The final contents should be reviewed during refactoring to ensure that
no business logic or service-specific implementation has been
inadvertently moved into this shared module.
