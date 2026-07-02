# StockPilot Backend

> A Spring Boot REST API for SME inventory and sales management with multi-tenant organization support, JWT security, email workflows, file storage, and async event processing.

## ✨ Overview

- 🚀 Built with Spring Boot and Java 21.
- 🗄️ Uses PostgreSQL with Flyway for schema management.
- 🔐 Implements JWT-based authentication and role-based authorization.
- 🧩 Supports organizations, users, roles, invitations, and profile management.
- 📬 Sends emails for verification, password reset, invitations, and account updates.
- 📦 Stores files and media in MinIO with presigned upload support.
- 🐇 Publishes and consumes audit events through RabbitMQ.
- ⚡ Uses Redis for caching/session-related infrastructure.
- 📘 Exposes OpenAPI/Swagger documentation for the REST API.

## 🧱 Tech Stack

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- Flyway
- PostgreSQL
- Redis
- RabbitMQ
- MinIO
- Thymeleaf
- SpringDoc OpenAPI
- Lombok
- MapStruct

## 📁 Project Structure

- `src/main/java/com/stockpilot/backend` - application entry point and feature modules.
- `src/main/java/com/stockpilot/backend/identity` - authentication, authorization, sessions, invitations, and audit events.
- `src/main/java/com/stockpilot/backend/org` - organization profile and storage workflows.
- `src/main/java/com/stockpilot/backend/shared` - common API responses, configuration, utilities, storage, and exceptions.
- `src/main/java/com/stockpilot/backend/tenant` - tenant-related domain and helpers.
- `src/main/resources/db/migration` - Flyway database migrations.
- `src/main/resources/templates` - email templates.
- `docker-compose.yml` - local infrastructure stack.

## 🔐 Main Capabilities

- 👤 User authentication with login, token refresh, logout, and email verification.
- 📨 Organization registration with automatic admin user provisioning.
- 🔁 Password reset and invitation acceptance flows.
- 👥 User administration with listing, activation, deactivation, role changes, and session revocation.
- 🛡️ Role discovery and permission-backed access control.
- 🏢 Organization profile and logo management.
- 🧾 Audit event publishing for security-sensitive actions.

## 📡 API Highlights

All routes are versioned under `/v1`.

- 🔑 Authentication
  - `POST /v1/auth/login/public`
  - `POST /v1/auth/register/public`
  - `POST /v1/auth/refresh/public`
  - `POST /v1/auth/logout/public`
  - `POST /v1/auth/logout-all/public`
  - `POST /v1/auth/forgot-password/public`
  - `POST /v1/auth/reset-password/public`
  - `GET /v1/auth/verify-email/public?token=...`
  - `POST /v1/auth/accept-invitation`
- 👤 Users
  - `GET /v1/users`
  - `GET /v1/users/me`
  - `GET /v1/users/{id}`
  - `POST /v1/users/invite`
  - `PATCH /v1/users/{id}/role`
  - `PATCH /v1/users/{id}/activate`
  - `PATCH /v1/users/{id}/deactivate`
  - `GET /v1/users/{id}/sessions`
  - `DELETE /v1/users/{id}/sessions/{sid}`
- 🛡️ Roles
  - `GET /v1/roles`
- 🏢 Organization
  - `GET /v1/org/profile`
  - `PATCH /v1/org/profile`
  - `GET /v1/org/logo`
  - `POST /v1/org/logo/presigned`
  - `PATCH /v1/org/logo/confirm`

## 🛠️ Prerequisites

- Java 21
- Maven 3.9+ or the included Maven wrapper
- Docker and Docker Compose
- PostgreSQL 16+
- Redis 7+
- RabbitMQ 3+
- MinIO

## ⚙️ Configuration

The application reads most settings from environment variables. Common required values include:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`
- `JWT_SECRET`
- `EMAIL_USER`
- `EMAIL_PASSWORD`
- `MINIO_ROOT_USER`
- `MINIO_ROOT_PASSWORD`

Useful optional values:

- `DB_NAME`
- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USER`
- `RABBITMQ_PASSWORD`
- `REDIS_HOST`
- `MINIO_ENDPOINT`

By default, the app expects:

- PostgreSQL at `jdbc:postgresql://localhost:5432/stockpilot`
- RabbitMQ at `localhost:5672`
- Redis at `localhost:6379`
- MinIO at `http://localhost:9000`
- Frontend at `http://localhost:5173`

## 🚀 Run Locally

### 1) Start infrastructure

```bash
docker compose up -d
```

### 2) Configure environment variables

- Set the required secrets in your shell or in a local `.env` file used by your runner.
- Make sure the database user/password match the values used by PostgreSQL.

### 3) Run the application

```bash
./mvnw spring-boot:run
```

On Windows, use:

```bash
mvnw.cmd spring-boot:run
```

### 4) Build and test

```bash
./mvnw test
./mvnw clean package
```

## 📘 API Documentation

- Swagger UI is provided through SpringDoc OpenAPI.
- The OpenAPI metadata is configured in `src/main/java/com/stockpilot/backend/shared/config/OpenApiConfig.java`.
- If your local setup uses the default SpringDoc path, the UI is typically available at `/swagger-ui/index.html`.

## 🧪 Testing

- The repository includes a Spring context smoke test in `src/test/java/com/stockpilot/backend/BackendApplicationTests.java`.
- Run the full test suite with `./mvnw test`.

## 📦 Infrastructure Notes

- Flyway runs automatically on startup and validates the schema.
- RabbitMQ is used for audit-related event publishing and consumption.
- MinIO handles organization logos and other stored objects via presigned uploads.
- Redis is available for supporting application state and caching concerns.

## 🤝 Contributing

- Keep changes focused and consistent with the existing package structure.
- Update migrations when the schema changes.
- Prefer DTOs and mappers at the API boundary.
- Add or update tests for behavior changes.

## 📄 License

- No explicit license file is present in this repository snapshot.
- Add a license before distributing the project externally.
