# InstituteConnect

InstituteConnect is a Spring Boot staff management API with JWT authentication and role-based access control. It provides employee, department, and role management endpoints, plus Swagger/OpenAPI docs and an in-memory H2 database for local development.

## Features

- Spring Boot 3.2.5 and Java 17
- JWT-based authentication
- Role-based authorization for admin and manager workflows
- Employee, department, and role management APIs
- Pagination and search for employee listings
- Swagger UI and OpenAPI documentation
- H2 in-memory database with seeded sample data

## Getting Started

### Prerequisites

- Java 17
- Maven, or the included Maven Wrapper on Windows (`mvnw.cmd`)

### Run the application

```powershell
.\mvnw.cmd spring-boot:run
```

The application starts on port `8082`.

### Run tests

```powershell
.\mvnw.cmd test
```

## Default Access

The application seeds sample roles, departments, and users on startup.

- Admin: `admin@institute.com` / `admin123`
- Manager: `manager@institute.com` / `manager123`
- Employee: `jane@institute.com` / `user123`

## API Docs and Database Console

- Swagger UI: `http://localhost:8082/swagger-ui.html`
- OpenAPI spec: `http://localhost:8082/api-docs`
- H2 Console: `http://localhost:8082/h2-console`

## Authentication Flow

1. Register or log in at `/api/auth/register` or `/api/auth/login`.
2. Copy the returned JWT access token.
3. Send the token in the `Authorization` header as `Bearer <token>`.

## Main Endpoints

- `POST /api/auth/register` - Register a new employee
- `POST /api/auth/login` - Log in and receive a JWT
- `GET /api/employees` - List employees with search and pagination
- `POST /api/employees` - Create an employee, admin only
- `PUT /api/employees/{id}` - Update an employee, admin or manager
- `DELETE /api/employees/{id}` - Delete an employee, admin only
- `POST /api/employees/{id}/roles` - Assign a role to an employee, admin only
- `GET /api/departments` - List departments
- `POST /api/departments` - Create a department, admin only
- `GET /api/roles` - List roles, admin only

## Tech Stack

- Spring Boot Web
- Spring Security
- Spring Data JPA
- H2 Database
- JJWT
- Springdoc OpenAPI

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).