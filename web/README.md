# User Registration and Authentication System - Web Frontend

This is the React web frontend for the User Registration and Authentication System.

## Setup Instructions

### Prerequisites
- Node.js 18+
- npm or yarn

### Installation

```bash
npm install
```

### Running the Application

Development server:
```bash
npm run dev
```

The application will be available at `http://localhost:5173`

Building for production:
```bash
npm run build
```

## Features

- **Register Page** - User registration with email, password, name
- **Login Page** - User login with email and password
- **Dashboard/Profile Page** - Protected page showing user profile information
- **JWT Authentication** - Token-based authentication
- **Protected Routes** - Routes that require authentication

## Tech Stack
- React 18.2
- React Router v6
- Axios for API calls
- Vite as build tool

## API Connection

The application connects to the backend at `http://localhost:8080/api`

Make sure the backend is running before starting the web application.
