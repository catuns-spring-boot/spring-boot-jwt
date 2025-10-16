# JWT Spring Boot Starter
A comprehensive, production-ready JWT authentication and authorization starter for Spring Boot applications with flexible configuration and optional domain entity support.

## Table of Contents

- Features
- Requirements
- Installation
- Quick Start
- Configuration
    - Basic Configuration
    - Advanced Configuration
    - Properties Reference
- Domain Entities
    - Using Starter's Abstract Entities
    - Custom Domain Implementation
- Security Configuration
    - Default Security Chain
    - Custom Security Chain
    - Filter Configuration


- Authentication
    - Built-in Authentication
    - Custom Authentication Provider
- Exception Handling
    - Default Exception Handlers
    - Custom Exception Handlers


- JWT Service

    - Token Generation
    - Token Validation
    - Custom JWT Service


- Usage Examples
- Testing
- Best Practices
- Troubleshooting
- API Reference

___

## Features
- ✅ Zero-Configuration Start - Works out of the box with sensible defaults
- ✅ Flexible Domain Support - Use built-in entities or bring your own
- ✅ Type-Safe Configuration - Full IDE autocomplete and compile-time validation
- ✅ Customizable Filters - JWT validation, generation, and exception handling
- ✅ Spring Security Native - Follows Spring Security DSL patterns
- ✅ CORS Support - Built-in CORS configuration
- ✅ Exception Handling - Comprehensive error responses with JSON formatting
- ✅ Property-Driven - Configure via application.yml or programmatically
- ✅ Production-Ready - Includes logging, metrics, and best practices

## Requirements

- Java 17 or higher
- Spring Boot 3.x
- Spring Security 6.x
- (Optional) Spring Data JPA for domain entities

___

## Installation

Maven

```xml
<dependency>
    <groupId>xyz.catuns.spring</groupId>
    <artifactId>jwt-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Gradle
```kts
implementation 'xyz.catuns.spring:jwt-spring-boot-starter:1.0.0-SNAPSHOT'
```

## Quick Start
1. Minimal Setup (No Domain Entities)
    ```java
    @SpringBootApplication
    @EnableJwtSecurity
    public class Application {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }
    ```
   ```properties
    # application.yml
    jwt:
        secret: your-secret-key-min-256-bits
    ```
   **That's it! Your application now has:**

    - JWT authentication enabled
    - Token validation on protected endpoints
    - Token generation on /auth/login
    - Stateless session management
    - JSON error responses
2. With Custom Domain Entities
    ```java
    @Entity
    @Table(name = "users")
    public class User extends UserEntity {

        @OneToMany(fetch = FetchType.EAGER)
        private Set<Role> roles = new HashSet<>();
    
        @Override
        public Collection<Role> getRoles() {
            return roles;
        }
    }
    
    @Entity
    @Table(name = "roles")
    public class Role extends RoleEntity {

        private String name;
    
        @Override
        public String getAuthority() {  
            return name;
        }
    
        @Override
        public String slugify() {
            return name.toLowerCase().replace(" ", "-");
        }
    }
    
    public interface UserRepository extends UserEntityRepository<User> {
    }
    
    @SpringBootApplication
    @EnableJwtSecurity(
        userEntityClass = User.class,
        roleEntityClass = Role.class,
        userRepositoryClass = UserRepository.class
    )
    public class Application {
        public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
        }
    }
    ```
___

## Configuration
Basic configuration
```yaml
jwt:
  enabled: true
  secret: ${JWT_SECRET}  # Use environment variable
  expiration: 1H    # 1 hour as Duration
  
  security:
    enabled: true
    public-paths:
      - /api/public/**
      - /auth/login
      - /auth/register
      - /actuator/health
```
Advanced configuration
```yaml
jwt:
  # Core JWT configuration
  enabled: true
  secret: ${JWT_SECRET}
  expiration: 1h
  refresh-expiration: 7d  # 7 days

  # Security configuration
  security:
    enabled: true
    public-paths:
      - /api/public/**
      - /auth/**
      - /swagger-ui/**
      - /v3/api-docs/**
    
    # CORS configuration
    cors:
      enabled: true
      allowed-origins:
        - https://myapp.com
        - https://admin.myapp.com
      allowed-methods:
        - GET
        - POST
        - PUT
        - DELETE
      allow-credentials: true
      max-age: 3600
    
    # Filter configuration
    filter:
      validator: true
      generator: true
      exception-handler: true
    
    # Token configuration
    validation:
      header-name: Authorization
      token-prefix: "Bearer "
    
    generation:
      header-name: Authorization
      token-prefix: "Bearer "
      expiration-header-name: X-Token-Expiration
    
    # Exception handling
    exception:
      include-message: true
      include-stack-trace: false  # true only in dev
      include-path: true
      log-exceptions: true

  # Authentication configuration
  authentication:
    enabled: true
    provider: username-password
    use-entity-service: true
  
  # Domain entity configuration
  entity:
    enabled: true
    packages:
      - com.myapp.domain
```

