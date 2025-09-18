# JWT Authentication Backend (Spring Boot/ Spring Security)

A modular backend build with Spring boot, JWT, Docker, PostgreSQL.
It includes brute-force protection, refresh token rotation, and  swagger API docs.
Designed for production authentication with a clear separation of concerns.

## Core Features :
- **JWT Authentication:** Stateless login with access and refresh tokens, claim extraction and token rotation.
- **Refresh Token Management:** Secure storage, validation, and revocation through `RefreshTokenService`.
- **Brute Force Protection:** Preventing brute force attacks by tracking login attempts, applies cooldown and sends violation messages.
- **H2-Based Unit Testing:** Uses an in-memory DB for reliable and fast tests.
- **Dockerized Stack:** PostgreSQL, pgAdmin GUI and Spring Boot app with docker compose.
- **Swagger UI:** Interactive API docs with endpoint details.
- **Global Exceptions Handling:** Central error handling with custom responses and validation feedback.
- **Email Verification an renewing token:** Sending verification email to new registred account with tracking expiry date of each token and possibilty of renewing the token after expiration.
- **CI Pipeline with GitHub Actions:** Automated tests on every push to `main`.
- **Environment Isolation:** `.env` config injection using dotenv, `.gitignore` hygiene.


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

## API Endpoints

| Method | Endpoint                  | Description |
|--------|---------------------------|-------------|
| POST   | `/api/auth/register`      | Register a new user |
| POST   | `/api/auth/login`         | Login and get access & refresh tokens |
| POST   | `/api/auth/refresh`       | Refresh access token using a valid refresh token |
| GET    | `/api/verify`             | Activates accounts if token is valid and not expired (requires Token) |
| POST   | `/api/renew`              | Renew verification token in case of expiration |


## Example Requests 


**Login**
```json
POST /api/aut/login
{
   "username":"test1",
   "password":"password@123"
}
```

## Testing
```bash
./mvnw test
```

Uses H2 in-memory DB with @ActiveProfiles("test")

## Contributing
Pull requests are welcome.
For major changes, open an issue first to discuss what you'd like to change.
Please make sure tests pass before submitting.

## Author
Created and maintained by **Oussama ELMESSAOUDI**.

Feel free to reach out on [GitHub](https://github.com/oussamaelmessaoudi) for questions or suggestions.

Check out my Medium article for a deeper look at Spring Security architecture or Implementing JWT authentication:

[Spring Security Architecture](https://medium.com/@oussamaelmessaoudi17)

[Implementing JWT Authentication](https://medium.com/@oussamaelmessaoudi17)

## License 
This project is licensed under the MIT License.

See the [LICENSE](LICENSE) file for more details.

