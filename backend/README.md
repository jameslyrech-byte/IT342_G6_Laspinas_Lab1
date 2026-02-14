# User Registration and Authentication System - Backend

This is the Spring Boot backend for the User Registration and Authentication System.

## Setup Instructions

### Prerequisites
- Java 17+
- Maven
- MySQL 8.0+

### Database Setup

Create a MySQL database:
```sql
CREATE DATABASE auth_db;
```

### Configuration

Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### Build and Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The backend will be available at `http://localhost:8080/api`

## API Endpoints

### Authentication
- **POST** `/api/auth/register` - Register a new user
- **POST** `/api/auth/login` - Login user

### User
- **GET** `/api/user/me` - Get current user profile (protected)

## Tech Stack
- Spring Boot 3.0
- Spring Security with JWT
- Spring Data JPA
- MySQL
- Lombok
