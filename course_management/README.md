# COURSE MANAGEMENT

Spring Boot project (courses, skills, users) used for learning

## Quick start

Requirements: Java 17+, Maven.

Build and run:

```bash
mvn clean package
mvn spring-boot:run
```

The app runs on `http://localhost:8080` by default.

## Security seeder

On application start the `SecuritySeeder` creates:
- Roles: `ROLE_USER`, `ROLE_ADMIN`.
- An auth user: username `admin` / password `admin@123` (AuthUser and a corresponding `User` record with profile are created).

This means you can authenticate with the seeded admin to obtain a JWT and test protected endpoints.

## API Routes (with dummy JSON)

All endpoints are prefixed as shown below.

### Authentication

- POST `/auth/login`
  - Request JSON:

```json
{ "username": "admin", "password": "admin@123" }
```

  - Response JSON:

```json
{ "token": "<jwt-token>" }
```

- POST `/auth/signup`
  - Request JSON (creates an auth user + a `User` profile if missing):

```json
{
  "username": "jdoe",
  "password": "pass123",
  "email": "jdoe@example.com",
  "fullName": "John Doe",
  "phone": "1234567890",
  "address": "123 Main St"
}
```

  - Response: plain text message `Successfull` (HTTP 201)

### Users

- POST `/users` (admin only) — create user with profile and optional courses

Request JSON example:

```json
{
  "username": "instructor1",
  "email": "instructor1@example.com",
  "profile": { "fullName": "Instructor One", "phone": "NA", "address": "NA" },
  "courses": [ { "title": "Intro to Java", "price": 49.99 } ]
}
```

- GET `/users/{id}` (admin only) — returns User object

- GET `/users/me` — returns current authenticated user's `User` record

- PUT `/users/me/profile` — update current user's profile

Request JSON:

```json
{ "fullName": "John Updated", "phone": "555-0000", "address": "New Address" }
```

- POST `/users/me/courses` — add a course for the current user

Request JSON:

```json
{ "title": "Advanced Spring", "price": 79.99 }
```

- PUT `/users/me/courses/{courseId}` — update a course owned by current user

- DELETE `/users/me/courses/{courseId}` — delete a course for current user

- POST `/users/me/skills/{skillId}` — attach an existing skill to current user

- DELETE `/users/me` — delete your account

### Courses

- POST `/courses` (admin only) — create course. The `instructor` must reference an existing `User` with `id`.

Request JSON:

```json
{
  "title": "Spring Boot Basics",
  "price": 29.99,
  "instructor": { "id": 2 }
}
```

- GET `/courses` — admin receives all courses; non-admin receives only their courses.

- PUT `/courses/{courseId}` (admin only) — update course fields and optionally change instructor

Request JSON (partial allowed):

```json
{ "title": "Spring Boot Advanced", "price": 39.99, "instructor": { "id": 3 } }
```

- DELETE `/courses/{id}` (admin only)

### Skills

- POST `/skills` (admin only)

Request JSON:

```json
{ "name": "Java" }
```

- GET `/skills` — admin receives all skills; non-admin receives only their skills

- PUT `/skills/{skillId}` (admin only)

- DELETE `/skills/{id}` (admin only)

## Testing with JWT

1. Obtain token via `/auth/login`.
2. Add header `Authorization: Bearer <token>` to subsequent requests.

Example curl to list courses (replace <token>):

```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/courses
```

## Notes

- The `SecuritySeeder` creates the roles and an `admin` user (auth + application `User` with profile). Use that account to manage protected endpoints or create other users with `/auth/signup`.
- The `Course` entity requires an `instructor` (ManyToOne). When creating or updating courses via the admin API, provide an `instructor` object containing an existing `User`'s `id`.

## Files added
- `.gitignore` contains sensible defaults for a Maven/Java project.


---
## Security Enhancements Added

This project now includes the following production‑grade security features:

### Rate Limiting (Resilience4j)
- Global rate limiter applied to all routes.
- Login‑specific rate limiter on `/auth/login` to prevent brute‑force attacks.

| Scope | Limit |
|------|-------|
| Global APIs | 20 requests per 20 seconds |
| Login API | 5 requests per 10 seconds |

Exceeding the limit returns:

```
HTTP 429 – Too many requests. Please slow down.
```

### SQL Injection Protection
A custom filter blocks malicious SQL patterns such as:

```
select, drop, update, delete, truncate, --, /* */, ;
```

Blocked requests return:

```
HTTP 400 – Malicious SQL input detected
```

### Caching
Service‑layer caching is enabled to reduce database load.

| Cache Name | Data Cached |
|------------|-------------|
| authUsers  | Authentication users for JWT |
| users      | User + Profile + Courses |
| courses    | All courses |
| skills     | All skills |

Caches are automatically evicted when data is modified.