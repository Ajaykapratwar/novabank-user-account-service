# NovaBank User Account Service

Spring Boot microservice for user authentication, authorization, user profiles, and bank account data in the NovaBank platform.

The service provides:

- User registration and login with stateless JWT authentication
- BCrypt password hashing and role-based authorization
- PostgreSQL persistence for users, roles, and accounts
- Kafka event publishing and balance-update consumption
- Actuator endpoints for health and monitoring
- Eureka client registration

## Technology stack

- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA / Hibernate
- PostgreSQL
- Apache Kafka
- Spring Cloud Netflix Eureka Client
- Maven
- Lombok, ModelMapper, and JJWT

## Project structure

```text
novabank-user-account-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/useraccountservice/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── enums/
│   │   │   ├── exceptions/
│   │   │   ├── kafka/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── UseraccountserviceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

## Prerequisites

- Java 21 or later
- Docker Desktop, if running Kafka with Docker Compose
- A PostgreSQL database
- A Kafka broker
- A Eureka server at `http://localhost:8761/eureka/` (when service discovery is enabled)

Maven is optional because the repository includes the Maven Wrapper.

## Configuration

Configuration is loaded from `src/main/resources/application.properties`. The application imports an optional `.env` file from the project root, and the following environment variables are required for a normal startup:

```dotenv
DB_URL_ACCOUNT=jdbc:postgresql://localhost:5432/novabank_users_account_db
DB_USERNAME=postgres
DB_PASSWORD=change-me
JWT_SECRET=replace-with-a-long-random-secret
JWT_EXPIRATION=86400000
```

The application defaults to:

| Setting | Default |
| --- | --- |
| HTTP port | `8081` |
| Kafka bootstrap server | `localhost:9092` |
| Kafka consumer group | `account-group` |
| Eureka server | `http://localhost:8761/eureka/` |
| JPA schema mode | `update` |

Do not commit `.env` or real credentials. The repository ignores `.env`; use a local copy containing environment-specific values.

## Start Kafka

The included Compose file starts Kafka only; it does not start PostgreSQL or Eureka.

```bash
docker compose up -d
```

Stop Kafka with:

```bash
docker compose down
```

## Run the application

### Linux or macOS

```bash
./mvnw spring-boot:run
```

### Windows PowerShell

```powershell
.\mvnw.cmd spring-boot:run
```

### Build and run the JAR

```bash
./mvnw clean package
java -jar target/useraccountservice-0.0.1-SNAPSHOT.jar
```

On Windows PowerShell:

```powershell
.\mvnw.cmd clean package
java -jar target\useraccountservice-0.0.1-SNAPSHOT.jar
```

### Run with Docker

Build the image and start the service:

```bash
docker build -t novabank-user-account-service .
docker run --rm --env-file .env -p 8081:8081 novabank-user-account-service
```

The container image uses Java 25 at runtime, while the Maven project targets Java 21.

## API

The service listens on `http://localhost:8081`.

### Public endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Register a user |
| `POST` | `/api/auth/login` | Authenticate and receive a JWT |
| `GET` | `/api/auth/hello` | Public welcome endpoint; the current controller declares a `LoginRequest` body |
| `GET` | `/actuator/**` | Actuator endpoints |

Registration request:

```json
{
  "email": "user@example.com",
  "password": "change-me",
  "firstName": "Jane",
  "lastName": "Doe",
  "role": "USER"
}
```

Login request:

```json
{
  "email": "user@example.com",
  "password": "change-me"
}
```

Send the returned token on protected requests:

```text
Authorization: Bearer <jwt>
```

### Authenticated endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/users/me` | Get the authenticated user's profile |
| `GET` | `/api/accounts/me` | Get the authenticated user's account |
| `GET` | `/api/accounts/{accountNumber}` | Get an account by account number |

### Admin endpoints

All admin endpoints require the `ADMIN` authority.

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/users/admin/search?email=...&accountNumber=...` | Search for a user |
| `GET` | `/api/users/admin/all` | List users, optionally filtered by `roleName` |
| `GET` | `/api/users/admin/stats` | Get user statistics |
| `PATCH` | `/api/users/admin/toggle-status/{userId}` | Toggle a user's status |
| `GET` | `/api/accounts/admin/all` | List accounts |
| `PATCH` | `/api/accounts/admin/status?accountNumber=...&status=...` | Change account status |

Supported account statuses are `ACTIVE`, `FROZEN`, and `CLOSED`. List endpoints use Spring pagination parameters such as `page`, `size`, and `sort`.

## Kafka integration

The service:

- Publishes user registration events to `user-registered-events`
- Consumes balance updates from `balance-update-events`
- Publishes balance-update notifications to `balance-update-notification-events`

The Kafka broker must be reachable at the configured `spring.kafka.bootstrap-servers` address.

## Security

The application is stateless and uses JWT authentication. Only `/api/auth/**` and `/actuator/**` are public; all other routes require authentication. Admin controllers additionally require the `ADMIN` authority.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
