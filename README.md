# Task Tracker Backend (Spring Boot)

Backend system for task tracking and team/project collaboration.

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Maven

## Features Implemented
- User authentication and authorization
  - Register
  - Login
  - Logout (token blacklist)
  - View/update profile
- Project collaboration
  - Create project
  - List user projects
  - Add members to project
- Task management
  - Create task
  - Update task
  - Delete task
  - Assign task to team member
  - Update task status
  - List assigned tasks with filtering/search/sorting
- Collaboration
  - Add and list comments on tasks
  - Add and list attachment metadata on tasks
- Validation and global error handling

## Project Structure
`src/main/java/com/tasktracker`
- `auth`: authentication DTOs, service, controller
- `config`: security config
- `security`: JWT and user details services
- `user`: user entity/repository/profile APIs
- `project`: project/team APIs
- `task`: task APIs + filtering/search/sorting
- `comment`: comment model and DTOs
- `attachment`: attachment metadata model and DTOs
- `exception`: global exception handling

## Setup
1. Create a PostgreSQL database:
   - Database: `tasktracker`
2. Update credentials in `src/main/resources/application.properties`.
3. Ensure `app.jwt.secret` is at least 32 characters.

## Run
```bash
mvn spring-boot:run
```

Build check:
```bash
mvn -DskipTests compile
```

## Auth Header
For secured endpoints:
```http
Authorization: Bearer <jwt-token>
```

## REST API Summary

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`

### User
- `GET /api/users/me`
- `PUT /api/users/me`

### Projects
- `POST /api/projects`
- `GET /api/projects`
- `POST /api/projects/{projectId}/members`

### Tasks
- `POST /api/tasks`
- `GET /api/tasks/my?status=OPEN&search=bug&sortBy=dueDate&sortDir=asc`
- `GET /api/tasks/{taskId}`
- `PUT /api/tasks/{taskId}`
- `DELETE /api/tasks/{taskId}`
- `PATCH /api/tasks/{taskId}/assign/{userId}`
- `PATCH /api/tasks/{taskId}/status?status=COMPLETED`

### Comments
- `POST /api/tasks/{taskId}/comments`
- `GET /api/tasks/{taskId}/comments`

### Attachments (metadata)
- `POST /api/tasks/{taskId}/attachments`
- `GET /api/tasks/{taskId}/attachments`
