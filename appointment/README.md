# Appointment Management System

A **Spring Boot REST API** for booking, rescheduling, cancelling, and searching patient appointments. Built for a college practical/assignment demonstrating layered architecture, DTOs, validation, exception handling, and MySQL persistence.

---

## 1. Project Description

The Appointment Management System lets patients book appointments with doctors, view them, reschedule them, cancel them (soft delete), and search/filter them. It enforces a business rule that a doctor cannot have two active appointments at the same date and time.

---

## 2. Features

- Book a new appointment
- View all appointments
- View a single appointment by ID
- Reschedule an appointment (with conflict checking)
- Cancel an appointment (soft delete — record is kept, status changes to `CANCELLED`)
- Search/filter appointments by patient name, doctor name, and/or status
- Centralized validation and exception handling with meaningful JSON errors
- Doctor double-booking prevention (`409 CONFLICT`)

---

## 3. Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3.3.4 | Application framework |
| Spring Web (MVC) | REST controllers |
| Spring Data JPA / Hibernate | ORM / database access |
| MySQL 8 | Relational database |
| Jakarta Bean Validation | Request validation (`@Valid`, `@NotBlank`, `@NotNull`) |
| Lombok | Reduces boilerplate (getters/setters/constructors) |
| Maven | Build tool / dependency management |
| Postman | API testing |

---

## 4. Architecture (Layered)

```
Controller  -->  Service (interface + impl)  -->  Repository  -->  MySQL
     ^                                                  
     |-- DTOs (Request/Response) used at the boundary
     |-- GlobalExceptionHandler (@RestControllerAdvice) catches errors
```

- **Controller** (`AppointmentController`) — handles HTTP requests/responses only.
- **Service** (`AppointmentService`, `AppointmentServiceImpl`) — contains all business logic and rules.
- **Repository** (`AppointmentRepository`) — Spring Data JPA interface, talks to MySQL.
- **Entity** (`Appointment`, `AppointmentStatus`) — JPA-mapped database model.
- **DTO** (`AppointmentRequest`, `RescheduleRequest`, `AppointmentResponse`) — clean data shapes exposed to clients, decoupled from the database entity.
- **Exception** (`GlobalExceptionHandler` + custom exceptions) — converts errors into consistent JSON responses with correct HTTP status codes.

### Folder Structure

```text
src/main/java/com/example/appointment
│
├── controller
│   └── AppointmentController.java
├── service
│   ├── AppointmentService.java
│   └── AppointmentServiceImpl.java
├── repository
│   └── AppointmentRepository.java
├── entity
│   ├── Appointment.java
│   └── AppointmentStatus.java
├── dto
│   ├── AppointmentRequest.java
│   ├── RescheduleRequest.java
│   └── AppointmentResponse.java
├── exception
│   ├── AppointmentNotFoundException.java
│   ├── AppointmentAlreadyCancelledException.java
│   ├── AppointmentConflictException.java
│   └── GlobalExceptionHandler.java
└── AppointmentApplication.java
```

---

## 5. Database Setup

1. Make sure MySQL Server is installed and running.
2. Open a MySQL client (Workbench, CLI, or DBeaver) and either:
   - Run the script in `sql/setup.sql`, **or**
   - Simply run: `CREATE DATABASE appointment_db;`
3. You do **not** need to create the `appointments` table manually — Hibernate creates/updates it automatically on application startup because of:
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```

### Configure your password

Open `src/main/resources/application.properties` and replace `YOUR_PASSWORD` with your actual MySQL root (or dedicated user) password:

```properties
spring.datasource.password=YOUR_PASSWORD
```

---

## 6. How to Run the Project

### Prerequisites
- Java 17+ installed (`java -version`)
- Maven installed (`mvn -version`) — or use an IDE like IntelliJ/Eclipse/VS Code with Maven support
- MySQL Server running on `localhost:3306`

### Steps

1. Unzip/clone the project.
2. Update the MySQL password in `application.properties` (see above).
3. From the project root, run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   Or simply run `AppointmentApplication.java` from your IDE.
4. The application starts on: `http://localhost:8080`
5. Test the APIs using Postman (see section below).

---

## 7. API Endpoints

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 1 | POST | `/api/appointments` | Book a new appointment |
| 2 | GET | `/api/appointments` | Get all appointments |
| 3 | GET | `/api/appointments/{id}` | Get appointment by ID |
| 4 | PUT | `/api/appointments/{id}/reschedule` | Reschedule an appointment |
| 5 | DELETE | `/api/appointments/{id}` | Cancel an appointment (soft delete) |
| 6 | GET | `/api/appointments/search?patientName=&doctorName=&status=` | Search/filter appointments |

---

## 8. Postman Testing Guide

> Base URL: `http://localhost:8080`
> Header for all POST/PUT requests: `Content-Type: application/json`

### 8.1 Book Appointment
- **Method:** POST
- **URL:** `/api/appointments`
- **Body (JSON):**
```json
{
  "patientName": "Piyush Chaudhari",
  "doctorName": "Dr. Sharma",
  "appointmentDate": "2026-08-25",
  "appointmentTime": "10:30:00",
  "reason": "Regular Checkup"
}
```
- **Expected Status:** `201 CREATED`
- **Expected Response:**
```json
{
  "id": 1,
  "patientName": "Piyush Chaudhari",
  "doctorName": "Dr. Sharma",
  "appointmentDate": "2026-08-25",
  "appointmentTime": "10:30:00",
  "reason": "Regular Checkup",
  "status": "BOOKED",
  "createdAt": "2026-08-22T10:00:00",
  "updatedAt": "2026-08-22T10:00:00"
}
```

### 8.2 Get All Appointments
- **Method:** GET
- **URL:** `/api/appointments`
- **Expected Status:** `200 OK`
- **Expected Response:** JSON array of appointment objects.

### 8.3 Get Appointment by ID
- **Method:** GET
- **URL:** `/api/appointments/1`
- **Expected Status:** `200 OK` (or `404 NOT FOUND` if the ID does not exist)
- **Expected Response (404 example):**
```json
{
  "timestamp": "2026-08-22T10:05:00",
  "status": 404,
  "message": "Appointment not found with id: 99"
}
```

### 8.4 Search Appointments
- **Method:** GET
- **URL:** `/api/appointments/search?patientName=Piyush`
- Other examples:
  - `/api/appointments/search?doctorName=Sharma`
  - `/api/appointments/search?status=BOOKED`
  - `/api/appointments/search?patientName=Piyush&status=BOOKED`
- **Expected Status:** `200 OK`
- **Expected Response:** JSON array of matching appointments.

### 8.5 Reschedule Appointment
- **Method:** PUT
- **URL:** `/api/appointments/1/reschedule`
- **Body (JSON):**
```json
{
  "appointmentDate": "2026-08-27",
  "appointmentTime": "14:00:00"
}
```
- **Expected Status:** `200 OK`
- **Expected Response:** Appointment object with updated date/time and `"status": "RESCHEDULED"`.

### 8.6 Cancel Appointment
- **Method:** DELETE
- **URL:** `/api/appointments/1`
- **Expected Status:** `200 OK`
- **Expected Response:** Appointment object with `"status": "CANCELLED"`.

### 8.7 Test Invalid Input (Validation)
- **Method:** POST
- **URL:** `/api/appointments`
- **Body (JSON) — missing patientName:**
```json
{
  "doctorName": "Dr. Sharma",
  "appointmentDate": "2026-08-25",
  "appointmentTime": "10:30:00",
  "reason": "Regular Checkup"
}
```
- **Expected Status:** `400 BAD REQUEST`
- **Expected Response:**
```json
{
  "timestamp": "2026-08-22T10:10:00",
  "status": 400,
  "message": "Validation failed",
  "errors": {
    "patientName": "Patient name is required"
  }
}
```

### 8.8 Test Appointment Conflict
- **Method:** POST
- **URL:** `/api/appointments`
- **Body:** Same doctor, same date/time as an existing active appointment (e.g. Dr. Sharma, 2026-08-25, 10:30:00).
- **Expected Status:** `409 CONFLICT`
- **Expected Response:**
```json
{
  "timestamp": "2026-08-22T10:15:00",
  "status": 409,
  "message": "Doctor is already booked at this date and time"
}
```

### 8.9 Test Rescheduling a Cancelled Appointment
- **Steps:** First cancel an appointment (8.6), then try to reschedule the same ID.
- **Method:** PUT
- **URL:** `/api/appointments/1/reschedule`
- **Body:**
```json
{
  "appointmentDate": "2026-08-28",
  "appointmentTime": "09:00:00"
}
```
- **Expected Status:** `400 BAD REQUEST`
- **Expected Response:**
```json
{
  "timestamp": "2026-08-22T10:20:00",
  "status": 400,
  "message": "Cannot reschedule appointment with id 1 because it is already cancelled"
}
```

---

## 9. Business Rules Summary

1. **Doctor conflict:** A doctor cannot have two active (non-cancelled) appointments at the same date and time → `409 CONFLICT`.
2. **Cancelled slots are free:** A cancelled appointment does not block a new booking at the same slot.
3. **No rescheduling cancelled appointments:** Attempting to reschedule a cancelled appointment → `400 BAD REQUEST`.
4. **No double cancellation:** Attempting to cancel an already-cancelled appointment → `400 BAD REQUEST`.
5. **Soft delete only:** Cancelling never removes the database row; it only updates `status` to `CANCELLED`.
6. **Validation:** `patientName` and `doctorName` must not be blank; `appointmentDate` and `appointmentTime` must not be null → otherwise `400 BAD REQUEST` with field-level error messages.

---

## 10. Viva / Concept Notes (Quick Reference)

- **Controller:** The entry point for HTTP requests. It maps URLs + HTTP methods (GET/POST/PUT/DELETE) to Java methods, and translates between HTTP and Java objects. It should contain no business logic.
- **Service:** Contains the actual business rules (e.g., conflict checking, status transitions). The Controller calls the Service; the Service calls the Repository. Using an interface (`AppointmentService`) + implementation (`AppointmentServiceImpl`) follows the dependency-inversion principle and makes the code easier to test/mock.
- **Repository:** An interface extending `JpaRepository<Appointment, Long>`. Spring Data JPA auto-generates the SQL implementation at runtime — no manual SQL needed for basic CRUD, though custom queries (like `search`) can be written with `@Query`.
- **DTO (Data Transfer Object):** A plain object used to transfer data between layers/clients, separate from the database Entity. This prevents leaking internal database structure to API clients and lets you shape the JSON exactly as needed.
- **JPA / Hibernate:** JPA (Jakarta Persistence API) is a specification for mapping Java objects to database tables (ORM). Hibernate is the implementation Spring Boot uses under the hood. Annotations like `@Entity`, `@Id`, `@GeneratedValue` define this mapping.
- **REST API:** An architectural style where resources (like "appointments") are exposed over HTTP using standard verbs: GET (read), POST (create), PUT (update), DELETE (remove), each with a specific URL and returning JSON.
- **Validation:** Jakarta Bean Validation annotations (`@NotBlank`, `@NotNull`) declared on DTO fields are automatically checked by Spring when the controller parameter is annotated with `@Valid`. Failures throw `MethodArgumentNotValidException`, which is caught centrally.
- **Exception Handling:** `@RestControllerAdvice` lets you define one class (`GlobalExceptionHandler`) that intercepts exceptions thrown anywhere in the app and converts them into a consistent JSON error response with the correct HTTP status — instead of scattering try/catch blocks everywhere.
- **MySQL:** The relational database that physically stores the `appointments` table. Spring Data JPA/Hibernate talks to it via JDBC using the connection details in `application.properties`.

---

## 11. Assignment Requirement Checklist

| Requirement | Satisfied By |
|---|---|
| Minimum 5 CRUD REST APIs | 6 endpoints implemented (book, get all, get by id, reschedule, cancel, search) |
| Layered architecture (Controller/Service/Repository) | `controller`, `service`, `repository` packages |
| DTOs | `AppointmentRequest`, `RescheduleRequest`, `AppointmentResponse` |
| Exception handling with `@ControllerAdvice` | `GlobalExceptionHandler` using `@RestControllerAdvice` |
| Input validation with `@Valid` + Bean Validation | `@Valid` in controller, `@NotBlank`/`@NotNull` in DTOs |
| Proper HTTP status codes | 200, 201, 400, 404, 409, 500 all implemented |
| Meaningful JSON responses | `AppointmentResponse` DTO + structured error bodies |
| MySQL database integration | Spring Data JPA + MySQL connector configured |
| APIs testable via Postman | See Postman Testing Guide (section 8) |
| README with description + endpoints | This file |

---

## 12. Notes

- This project intentionally does **not** use Spring Security, as instructed.
- Cancellation is a **soft delete** — the database row is never physically removed.
- Timestamps (`createdAt`, `updatedAt`) are managed automatically via JPA lifecycle callbacks (`@PrePersist`, `@PreUpdate`).
