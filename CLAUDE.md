# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

### Build the project
```bash
mvn clean install
```

### Build without running tests
```bash
mvn clean install -DskipTests
```

### Run all tests
```bash
mvn clean test
```

### Run tests for a specific module
```bash
mvn test -pl jwt-core
```

### Run a specific test class
```bash
mvn test -Dtest=JwtTokenValidatorFilterTest
```

### Run tests matching a pattern
```bash
mvn test -Dtest=*TokenProvider*
```

### Build and deploy to local Maven repository
```bash
mvn clean install
```

### Deploy to GitHub Packages repository
```bash
mvn clean deploy
```

## Project Overview

**Spring Boot JWT Starter** is a production-ready JWT authentication and authorization Spring Boot starter library that provides:
- Zero-configuration JWT security out-of-the-box
- Flexible domain entity support (UserEntity, RoleEntity)
- Type-safe property configuration
- Customizable security filters
- Comprehensive exception handling with JSON responses
- Built-in CORS support

**Current Version**: 1.0.2-SNAPSHOT
**Java Version**: 21
**Spring Boot**: 3.5.6
**Spring Security**: 6.x

## Codebase Architecture

The project uses a **modular, layered architecture** organized into 7 Maven modules with clear separation of concerns:

### Module Structure & Dependencies

```
spring-boot-jwt (root pom)
├── jwt-dependencies
│   └── Manages versions for all internal modules & external dependencies
│       (JJWT 0.12.6, Spring Boot 3.5.6, Spring Data 2025.0.4, Lombok 1.18.38)
│
├── jwt-core
│   └── Core abstractions & models
│       ├── TokenProvider<T> interface (generic token generation/validation)
│       ├── JwtToken record (token value + metadata)
│       ├── TokenValidator<T> functional interface (extensible validation)
│       └── TokenValidationException (base exception)
│
├── jwt-domain
│   └── Optional domain entity support
│       ├── UserEntity abstract class (extends UserDetails)
│       ├── RoleEntity abstract class (extends GrantedAuthority)
│       ├── UserEntityRepository<E> generic repository interface
│       ├── TokenEntityRepository interface
│       └── DomainMetadata (holder for domain config)
│
├── jwt-auth
│   └── Authentication & token providers
│       ├── AbstractTokenProvider<T> (base JJWT implementation)
│       ├── AuthTokenProvider (concrete provider for Authentication objects)
│       ├── JwtCustomizer<T> functional interface (customize JWT claims)
│       ├── UsernamePwdAuthenticationProvider
│       ├── UserEntityService<E> (loads users from repository)
│       └── EmailNotFoundException
│
├── jwt-security
│   └── Security filters & exception handling
│       ├── JwtTokenValidatorFilter (validates JWT, sets auth context)
│       ├── JwtTokenGeneratorFilter (generates JWT after successful auth)
│       ├── JwtExceptionHandlerFilter (catches & translates exceptions)
│       ├── JwtSecurityConfigurer (Spring Security DSL configurer)
│       ├── JwtFilterConfigurer (builder for filter configuration)
│       ├── JwtAuthenticationEntryPoint (handles 401 Unauthorized)
│       ├── JwtAccessDeniedHandler (handles 403 Forbidden)
│       └── JwtExceptionHandlingConfigurer
│
├── jwt-autoconfigure
│   └── Spring Boot auto-configuration
│       ├── @EnableJwtSecurity annotation
│       ├── JwtAutoConfiguration (creates token provider bean)
│       ├── JwtSecurityAutoConfiguration (registers filters & handlers)
│       ├── JwtAuthenticationAutoConfiguration (registers auth components)
│       ├── JwtDomainRegistrar (ImportBeanDefinitionRegistrar for domain scanning)
│       └── JwtProperties configuration properties class
│
└── jwt-spring-boot-starter
    └── Starter bundle packaging all modules
```

### JWT Token Flow

**Generation**: `UsernamePasswordAuthenticationFilter` → `JwtTokenGeneratorFilter` → Response headers
**Validation**: Request `JwtTokenValidatorFilter` → Validate JWT → Set `SecurityContext`

**Claims Structure** (AuthTokenProvider):
- `issuer`: Configured issuer string
- `subject`: Username from authentication
- `user`: Principal object
- `authorities`: Comma-separated list of GrantedAuthority names
- `iat`: Issued-at timestamp (automatic)
- `exp`: Expiration timestamp (automatic)

### Security Filter Chain Order

1. **JwtExceptionHandlerFilter** (before LogoutFilter)
   - Wraps entire chain in try-catch
   - Delegates exceptions to Spring MVC HandlerExceptionResolver

2. **JwtTokenValidatorFilter** (before UsernamePasswordAuthenticationFilter)
   - Extracts JWT from request header
   - Validates signature and expiration
   - Sets authentication in SecurityContext if valid
   - Skips if authentication already exists

3. **UsernamePasswordAuthenticationFilter** (Spring default)
   - Handles form-based username/password authentication

4. **JwtTokenGeneratorFilter** (after UsernamePasswordAuthenticationFilter)
   - Generates JWT token after successful authentication
   - Writes token to response headers: `Authorization: Bearer <token>`
   - Optionally writes expiration to: `X-Token-Expiration: <instant>`

### AutoConfiguration & @EnableJwtSecurity

The `@EnableJwtSecurity` annotation triggers a conditional bean registration chain:

1. **JwtAutoConfiguration** (earliest)
   - Reads `jwt.secret`, `jwt.issuer`, `jwt.expiration` from properties
   - Creates `AuthTokenProvider` bean

2. **JwtSecurityAutoConfiguration** (conditional: `jwt.security.enabled=true`)
   - Registers `JwtAuthenticationEntryPoint` (401)
   - Registers `JwtAccessDeniedHandler` (403)
   - Creates `CorsConfigurationSource` from `jwt.security.cors.*` properties
   - Registers all three JWT filters with proper ordering
   - Creates `SecurityFilterChain` bean with JWT filters

3. **JwtAuthenticationAutoConfiguration** (conditional: `jwt.auth.enabled=true`)
   - Registers `AuthenticationManager`
   - Registers `PasswordEncoder`
   - Conditionally registers `UserEntityService<E>` (if domain entities configured)
   - Conditionally registers `UsernamePwdAuthenticationProvider`

**Domain Configuration** (optional):
- If `@EnableJwtSecurity(userEntityClass=X.class, roleEntityClass=Y.class)` specified
- `JwtDomainRegistrar` scans classpath packages and registers domain entities
- `UserEntityService` uses configured repository for user loading

### Configuration Properties

**Core JWT Configuration** (`jwt.*`):
```yaml
jwt:
  enabled: true                    # Enable JWT support
  secret: "${JWT_SECRET}"          # Required, min 256 bits
  issuer: "app-name"               # Optional
  expiration: 10h                  # Default: 10 hours, supports Duration format
  refresh-expiration: 7d           # Optional, supports Duration format
```

**Security Configuration** (`jwt.security.*`):
```yaml
jwt.security:
  enabled: true
  public-paths: ["/login", "/health"]

  filter:
    validator: true                # Enable token validation filter
    generator: true                # Enable token generation filter
    exception-handler: true        # Enable exception handling filter
    order: -100                    # Filter chain order (lower executes first)

  cors:
    enabled: true
    allowed-origins: ["http://localhost:3000"]
    allowed-methods: [GET, POST, PUT, DELETE, OPTIONS]
    allow-credentials: true
    max-age: 3600

  validation:
    header-name: "Authorization"
    token-prefix: "Bearer "

  generation:
    header-name: "Authorization"
    token-prefix: "Bearer "
    expiration-header-name: "X-Token-Expiration"

  exception:
    include-message: true          # Include exception message in response
    include-path: true             # Include request path in response
    include-stack-trace: false     # Set to true only in dev environments
    log-exceptions: true           # Log exceptions via SLF4J
```

**Authentication Configuration** (`jwt.auth.*`):
```yaml
jwt.auth:
  enabled: true
  provider: username-password
  use-entity-service: true         # Use domain UserEntityService for user loading
```

**Domain Entity Configuration** (`jwt.entity.*`):
```yaml
jwt.entity:
  enabled: true
  packages:
    - com.myapp.domain             # Scan packages for UserEntity/RoleEntity subclasses
```

## Key Extension Points

### 1. Custom JWT Claims
Create a bean that configures the token provider with a custom `JwtCustomizer`:
```java
@Bean
public AuthTokenProvider authTokenProvider(JwtProperties props) throws MissingSecretException {
    AuthTokenProvider provider = new AuthTokenProvider(props.getSecret(), props.getIssuer(), props.getExpiration());
    provider.setCustomizer((jwt, auth) -> {
        jwt.claim("userId", getUserId(auth))
           .claim("tenant", getTenant(auth));
    });
    return provider;
}
```

### 2. Custom Token Validation
Override `AuthTokenProvider` with custom validator:
```java
provider.setValidator((claims) -> {
    if (isTokenBlacklisted(claims.getJti())) {
        throw new TokenValidationException("Token revoked");
    }
});
```

### 3. Custom Domain Entities
Extend `UserEntity` and `RoleEntity` for database-backed user management:
```java
@Entity
public class User extends UserEntity {
    @OneToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;

    @Override
    public Collection<? extends RoleEntity> getRoles() {
        return roles;
    }
}

public interface UserRepository extends UserEntityRepository<User> {}

@EnableJwtSecurity(userEntityClass = User.class, roleEntityClass = Role.class, userRepositoryClass = UserRepository.class)
```

### 4. Custom Authentication Provider
Override the default username/password provider with custom logic:
```java
@Bean
public AuthenticationProvider customAuthProvider() {
    return new CustomAuthenticationProvider(...);
}
```

### 5. Custom Exception Handlers
Override default 401/403 handlers for custom error response formats:
```java
@Bean
public AuthenticationEntryPoint customEntryPoint() {
    return (request, response, authException) -> {
        // Custom 401 response
    };
}

@Bean
public AccessDeniedHandler customAccessDeniedHandler() {
    return (request, response, exception) -> {
        // Custom 403 response
    };
}
```

### 6. Custom Filter Conditions
Use predicates to customize when filters apply:
```java
http.with(JwtSecurityConfigurer.jwt(), jwt -> jwt
    .filterConfigurer(fc -> fc
        .validatorPredicate(req -> !req.getRequestURI().startsWith("/public"))
        .generatorPredicate(req -> req.getRequestURI().equals("/login"))
    )
);
```

## Common Development Tasks

### Adding a New Feature to a Module
1. Identify the appropriate module based on the responsibility breakdown above
2. Add the feature class following existing naming conventions
3. Add unit tests in the same module's `src/test/java`
4. If the feature needs to be auto-configured, add conditional bean in `jwt-autoconfigure`
5. Update README.md if it's a public-facing feature
6. Run `mvn clean test` to ensure no regressions

### Modifying Security Filters
Filters are in `jwt-security/src/main/java/xyz/catuns/spring/jwt/security/filters/`:
- Filter logic uses Spring Security conventions
- `JwtSecurityConfigurer` manages filter chain positioning
- Remember: filters run in order, so positioning matters for behavior
- Always consider interaction with Spring's standard filters

### Extending Domain Support
Domain classes are in `jwt-domain/src/main/java/xyz/catuns/spring/jwt/domain/`:
- `UserEntity` must be extended, not used directly
- `RoleEntity` must be extended with `getAuthority()` and `slugify()` implementations
- Repository interfaces extend `UserEntityRepository<E>` or `TokenEntityRepository`
- Domain scanning happens via `@EnableJwtSecurity` annotation parameters

### Testing Domain Entities
Domain entity tests live in `jwt-domain/src/test/`:
- Use `@DataJpaTest` for repository testing
- Mock `DomainMetadata` when testing domain registration
- Test custom `slugify()` implementations in role entities

## Troubleshooting Common Issues

### "jwt.secret is required"
Ensure `jwt.secret` property is set in `application.yml` or via environment variable `JWT_SECRET`

### Filter chain not validating tokens
Check that `jwt.security.enabled=true` and `jwt.security.filter.validator=true`

### Token generation not working
Verify `jwt.security.filter.generator=true` and request is being authenticated by `UsernamePasswordAuthenticationFilter`

### Custom domain entities not loading
Ensure `@EnableJwtSecurity` specifies correct `userEntityClass`, `roleEntityClass`, and `userRepositoryClass`
Verify domain classes are in scanned packages (default: all packages)

### 401/403 responses not in JSON format
Confirm `jwt.security.enabled=true` so `JwtAuthenticationEntryPoint` and `JwtAccessDeniedHandler` are registered
