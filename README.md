# JWT Authentication Backend (Spring Boot/ Spring Security)

A modular backend build with Spring boot, JWT, Docker, PostgreSQL.
It includes brute-force protection, refresh token rotation, and  swagger API docs.
Designed for production authentication with a clear separation of concerns.

## Core Features :
- **JWT Authentication** - Stateless login with access and refresh tokens, claim extraction and token rotation.
- **Refresh Token Management** - Secure storage, validation, and revocation through 'RefreshTokenService"

** Will complete featuers later on** 

## Project Structure
```text
src/
├── main/
│ ├── java/com/jwt/jwt/
│ │ ├── config/ # Security config, Swagger setup
│ │ ├── controller/ # Auth endpoints (login, register, refresh)
│ │ ├── dto/ # Request/response payloads
│ │ ├── entity/ # User, RefreshToken, LoginAttempt
│ │ ├── enumeration/ # Role definitions
│ │ ├── exception/ # Global exception handling
│ │ ├── repository/ # JPA repositories
│ │ ├── service/ # Business logic (auth, token, login tracking)
│ │ ├── util/ # JWT utilities, claim extraction
│ │ ├── validation/ # Custom validators and cooldown logic
│ │ └── JwtApplication.java
│ └── resources/
│ └── application.properties
├── test/
│ ├── java/com/jwt/jwt/
│ │ └── JwtApplicationTests.java
│ └── resources/
│ └── application-test.properties
```

## Setup 
```bash
git clone https://github.com/oussamaelmessaoudi/Auth-API-Java
cd Auth-API-Java
cp .env.example .env
docker-compose up --build
```

## Access Points
- **App;** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **pgAdmin:** http://localhost:5050

## Testing
```bash
./mvnw test
```

Uses H2 in-memory DB with @ActiveProfiles("test")

***Currently working on email verification
