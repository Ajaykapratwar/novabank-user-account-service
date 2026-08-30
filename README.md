# NovaBank User Account Service

A Spring Boot microservice for managing user authentication, authorization, and account-related user data for the NovaBank platform.

This project provides the foundational user account layer for a banking application, including user registration, login, JWT-based security, database persistence, and Kafka-ready event integration.

## Overview

NovaBank User Account Service is designed to handle:

- User registration and login
- JWT-based authentication
- Role-based authorization
- PostgreSQL-backed persistence for users, roles, and account records
- Kafka integration for event-driven messaging
- Secure Spring Security configuration for protected API access

## Features

- Java 21 + Spring Boot 4.1.1
- Spring Security with JWT support
- JPA / Hibernate persistence with PostgreSQL
- Role management using `Role` and `User` entities
- Account domain models for banking use cases
- Event-driven messaging via Kafka
- Actuator health and metrics endpoints
- Docker Compose support for Kafka setup

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- PostgreSQL
- Kafka
- Maven
- Lombok
- ModelMapper
- JJWT (JWT library)

## Project Structure

```text
novabank-user-account-service/
├── src/
│   ├── main/
│   │   ├── java/com/example/useraccountservice/
│   │   │   ├── config/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── enums/
│   │   │   ├── exceptions/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── UseraccountserviceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/useraccountservice/
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── LICENSE
├── HELP.md
└── README.md
```

## Prerequisites

Before running this service, make sure you have:

- Java 21 or later
- Maven 3.9+
- PostgreSQL running locally or in a container
- Kafka running locally or via Docker

## Configuration

The application configuration is stored in `src/main/resources/application.properties`.

Default configuration includes:

- Server port: `8081`
- PostgreSQL database: `novabank_users_account_db`
- PostgreSQL host: `localhost:5432`
- Kafka bootstrap server: `localhost:9092`
- JWT secret and expiration values configured through properties

You should update the database credentials and JWT secret before using this in a real environment.

Example values in `application.properties`:

```properties
spring.application.name=user-account-service
server.port=8081

spring.datasource.url=jdbc:postgresql://localhost:5432/novabank_users_account_db
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update

jwt.secret=your_very_long_and_very_secure_secrete_key_for_authentication_it_should_be_at_least_64_characters_long
jwt.expiration=86400000

spring.kafka.bootstrap-servers=localhost:9092
```

## Running with Docker

This project includes Kafka support via Docker Compose.

```bash
docker compose up -d
```

This starts the Kafka broker used by the application.

## Running the Application

### Using Maven

```bash
./mvnw clean install
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

### Build the JAR

```bash
./mvnw clean package
java -jar target/useraccountservice-0.0.1-SNAPSHOT.jar
```

## Security

The service uses Spring Security with stateless JWT authentication.

- Public routes are allowed under `/api/auth/**` and `/actuator/**`
- All other requests require authentication
- Passwords are encoded using BCrypt
- JWT tokens are generated and validated in `JwtService`

## API Overview

The application is structured around authentication and user/account services. The security configuration includes the public auth route prefix:

- `POST /api/auth/register`
- `POST /api/auth/login`

Additional protected endpoints can be added as the system expands.

## Main Components

- `User` — user model with email, password, roles, and status
- `Role` — user role entity
- `Account` — banking account entity
- `AuthService` — registration and login operations
- `JwtService` — JWT generation and validation
- `SecurityConfig` — HTTP security rules and BCrypt setup
- `UserRepository` — repository for user persistence

## Contributing

Contributions are welcome. Please:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a pull request

## License

This project is licensed under the MIT License. See the `LICENSE` file for details.

## Notes

This repository is a strong starting point for a banking identity/authentication service and can be extended with:

- user profile management
- email verification
- password reset flows
- account creation and transaction workflows
- event publishing for account updates and notifications

