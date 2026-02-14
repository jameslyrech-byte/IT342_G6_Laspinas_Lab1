# User Registration and Authentication System
## Functional Requirements Specification (FRS)

---

## 1. Project Overview

This document specifies the functional requirements for a User Registration and Authentication System consisting of a Spring Boot backend and a ReactJS web frontend.

### Project Scope
- **Backend**: Spring Boot 3.0 with Spring Security, JWT authentication, and MySQL database
- **Frontend**: ReactJS with React Router for client-side routing
- **Database**: MySQL with encrypted password storage using BCrypt

---

## 2. System Architecture

### Technology Stack

**Backend:**
- Java 17
- Spring Boot 3.0
- Spring Security
- Spring Data JPA
- MySQL 8.0
- JWT (JSON Web Tokens)
- Lombok
- Maven

**Frontend:**
- React 18.2
- React Router v6
- Axios
- Vite
- CSS3

---

## 3. Database Design

### Entity Relationship Diagram (ERD)

```
┌─────────────────────────────┐
│         USERS               │
├─────────────────────────────┤
│ id (PK)                     │
│ email (UNIQUE)              │
│ firstName (VARCHAR)         │
│ lastName (VARCHAR)          │
│ password (VARCHAR - Bcrypt) │
│ createdAt (TIMESTAMP)       │
│ updatedAt (TIMESTAMP)       │
└─────────────────────────────┘
```

### Table Description - Users

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique user identifier |
| email | VARCHAR(255) | UNIQUE, NOT NULL | User email address |
| firstName | VARCHAR(100) | NOT NULL | User's first name |
| lastName | VARCHAR(100) | NOT NULL | User's last name |
| password | VARCHAR(255) | NOT NULL | Encrypted password (BCrypt) |
| createdAt | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Account creation date |
| updatedAt | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP ON UPDATE | Last update date |

---

## 4. UML Diagrams

### Class Diagram - Backend

```
┌──────────────────────────┐
│       User (Entity)      │
├──────────────────────────┤
│ - id: Long               │
│ - email: String          │
│ - firstName: String      │
│ - lastName: String       │
│ - password: String       │
│ - createdAt: LocalDateTime
│ - updatedAt: LocalDateTime
├──────────────────────────┤
│ + getters/setters        │
└──────────────────────────┘
           ▲
           │
     ┌─────┴──────────────────┐
     │                        │
┌────────────────┐  ┌──────────────────┐
│ UserRepository │  │   AuthService    │
├────────────────┤  ├──────────────────┤
│ - JpaRepository│  │ + register()     │
│ + findByEmail()│  │ + login()        │
└────────────────┘  │ + getUser()      │
                    └──────────────────┘
```

### Sequence Diagram - Registration Flow

```
User → RegisterPage → AuthService → UserRepository → Database
  │         │            │              │              │
  │──Register─────────────→             │              │
  │         │            │              │              │
  │         │──validate──→              │              │
  │         │            │              │              │
  │         │            │──check email─────────────→  │
  │         │            │              │←──exists?────│
  │         │            │──encrypt pwd─│              │
  │         │            │──save user───────────────→  │
  │         │            │              │←──success────│
  │←─response────────────│              │              │
```

### Sequence Diagram - Login Flow

```
User → LoginPage → AuthService → UserRepository → JwtTokenProvider
  │       │           │              │                  │
  │──Login─────────────→             │                  │
  │       │           │              │                  │
  │       │──validate──→             │                  │
  │       │           │──find user───────────────→     │
  │       │           │              │←──user data───│
  │       │           │──verify pwd──│                  │
  │       │           │──generate token───────────────→│
  │       │           │              │         ←──token─│
  │←─response────────│              │                  │
```

---

## 5. Functional Requirements

### 5.1 Backend API Endpoints

#### 5.1.1 Registration Endpoint
- **Request**: `POST /api/auth/register`
- **Request Body**:
```json
{
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "password": "Password123",
  "confirmPassword": "Password123"
}
```
- **Success Response (201):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```
- **Error Response (400):**
```json
{
  "success": false,
  "message": "Email already exists"
}
```

#### 5.1.2 Login Endpoint
- **Request**: `POST /api/auth/login`
- **Request Body**:
```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```
- **Success Response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```
- **Error Response (401):**
```json
{
  "success": false,
  "message": "Invalid email or password"
}
```

#### 5.1.3 Get User Profile (Protected)
- **Request**: `GET /api/user/me`
- **Headers**: `Authorization: Bearer {token}`
- **Success Response (200):**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```
- **Error Response (401):**
```json
{
  "success": false,
  "message": "Unauthorized"
}
```

### 5.2 Frontend Pages

#### 5.2.1 Register Page
- **Path**: `/register`
- **Components**:
  - Form with fields: First Name, Last Name, Email, Password, Confirm Password
  - Submit button
  - Link to login page
  - Success/Error message display
- **Functionality**:
  - Client-side and server-side validation
  - Password and confirm password matching
  - Minimum password length (6 characters)
  - Email format validation
  - Disable submit button while submitting
  - Redirect to login on successful registration

#### 5.2.2 Login Page
- **Path**: `/login`
- **Components**:
  - Form with fields: Email, Password
  - Submit button
  - Link to register page
  - Error message display
- **Functionality**:
  - Client-side validation
  - Store JWT token in localStorage
  - Redirect to dashboard on successful login
  - Display error message on failed login

#### 5.2.3 Dashboard/Profile Page
- **Path**: `/dashboard` (Protected)
- **Components**:
  - User profile information card
  - Display: First Name, Last Name, Email, User ID
  - Logout button in header
- **Functionality**:
  - Fetch user data from protected API endpoint
  - Redirect to login if not authenticated
  - Clear local storage on logout
  - Redirect to login after logout

#### 5.2.4 Protected Routes
- **Implementation**: ProtectedRoute component
- **Behavior**: Redirects unauthenticated users to login page
- **Protected Pages**: Dashboard

---

## 6. Authentication & Security

### 6.1 Password Encryption
- Passwords are encrypted using **BCrypt** with a salt rounds of 10
- Passwords are NEVER stored in plain text

### 6.2 JWT Token
- **Algorithm**: HS256
- **Expiration**: 24 hours (86400000 milliseconds)
- **Token Structure**: `{email, userId, issuedAt, expiration}`
- **Storage (Frontend)**: localStorage
- **Transmission**: Authorization header with Bearer scheme

### 6.3 CORS Configuration
- **Allowed Origins**: 
  - http://localhost:3000
  - http://localhost:5173
- **Allowed Methods**: GET, POST, PUT, DELETE, OPTIONS
- **Allowed Headers**: All

---

## 7. API Integration Points

### 7.1 Frontend API Service
```javascript
// Registration
POST http://localhost:8080/api/auth/register

// Login
POST http://localhost:8080/api/auth/login

// Get Profile (requires token)
GET http://localhost:8080/api/user/me
Header: Authorization: Bearer {token}
```

### 7.2 Token Management
- Token is stored in localStorage after login
- Token is automatically attached to protected requests via Axios interceptor
- Token is removed on logout

---

## 8. Error Handling

### Backend Errors
| Status Code | Scenario | Message |
|-------------|----------|---------|
| 200 | Success | "Login successful" or "User retrieved successfully" |
| 201 | Created | "User registered successfully" |
| 400 | Validation Error | Various validation messages |
| 401 | Unauthorized | "Invalid email or password" or "Unauthorized" |
| 404 | Not Found | "User not found" |

### Frontend Error Handling
- Display error messages in modal/alert format
- Catch API errors and display to user
- Redirect to login on 401 responses
- Prevent form submission on validation errors

---

## 9. User Interface Mockups

### 9.1 Register Page
The registration page is styled with a modern gradient background (purple theme). Features include:
- Clean white form box with rounded corners
- Input fields for: First Name, Last Name, Email, Password, Confirm Password
- Blue gradient submit button with hover effects
- Link to login page for existing users
- Success/error message display at the top

### 9.2 Login Page
Similar styling to register page:
- White form box
- Email and Password input fields
- Blue gradient submit button
- Link to register page for new users
- Error message display

### 9.3 Dashboard Page
- Header with "Dashboard" title and red logout button
- White profile card with user information
- Displays: First Name, Last Name, Email, User ID
- Clean grid layout for information display
- Purple gradient background

### 9.4 Color Scheme
- **Primary Gradient**: #667eea to #764ba2 (Purple)
- **Secondary**: White (#FFFFFF)
- **Text**: Dark gray (#333333)
- **Error**: Red (#c33)
- **Success**: Green (#3c3)
- **Accent**: Purple (#667eea)

---

## 10. Non-Functional Requirements

### 10.1 Performance
- API response time < 500ms
- Page load time < 2s

### 10.2 Security
- All passwords are encrypted with BCrypt
- JWT tokens are used for secure transmission
- CORS is properly configured
- SQL Injection prevention through parameterized queries (JPA)
- XSS prevention through React's built-in escaping

### 10.3 Scalability
- Database queries are indexed on frequently searched columns (email)
- JWT tokens are stateless and don't require server-side storage

### 10.4 Compatibility
- Supports all modern browsers (Chrome, Firefox, Safari, Edge)
- Responsive design for desktop and tablet

---

## 11. Testing Scenarios

### 11.1 Registration Testing
- [ ] Valid registration with complete data
- [ ] Duplicate email rejection
- [ ] Password mismatch rejection
- [ ] Short password rejection (< 6 characters)
- [ ] Empty field validation
- [ ] Invalid email format rejection

### 11.2 Login Testing
- [ ] Successful login with valid credentials
- [ ] Failure with non-existent email
- [ ] Failure with incorrect password
- [ ] Token storage verification
- [ ] Dashboard accessibility with valid token

### 11.3 Dashboard Testing
- [ ] Redirect to login when accessing without token
- [ ] Display correct user information
- [ ] Logout functionality clears token
- [ ] Logout redirects to login page

---

## 12. Database Initial Setup

```sql
CREATE DATABASE auth_db;

USE auth_db;

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  firstName VARCHAR(100) NOT NULL,
  lastName VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_email (email)
);
```

---

## 13. Deployment Considerations

### Backend Deployment
- Requires Java 17 runtime
- Set environment variables for DB connection
- Change JWT secret from default
- Update CORS origins for production

### Frontend Deployment
- Build for production with `npm run build`
- Serve static files from web server
- Update API URL for production environment
- Use HTTPS for secure communication

---

## Document Info
- **Version**: 1.0
- **Date**: February 2026
- **Status**: Complete - Session 1 Implementation
