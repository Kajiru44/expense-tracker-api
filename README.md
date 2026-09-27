# Expense Tracker API

A small REST API for managing personal expenses.

The project was built with Java 21, Spring Boot 4.1.1, Spring Data JPA and PostgreSQL. It provides a simple CRUD API for creating, reading, updating and deleting expenses.

## Technologies

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Jakarta Validation
- Maven
- JUnit 5
- Mockito
- MockMvc

## Features

- Create expenses
- Get all expenses
- Get a single expense by ID
- Update expenses
- Delete expenses
- Input validation
- Global exception handling
- Custom 404 error handling
- Unit tests
- Controller tests

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.kajiru.expense_tracker_api
│   │       ├── controller
│   │       │   └── ExpenseController.java
│   │       ├── exception
│   │       │   ├── ExpenseNotFoundException.java
│   │       │   └── GlobalExceptionHandler.java
│   │       ├── model
│   │       │   └── Expense.java
│   │       ├── repository
│   │       │   └── ExpenseRepository.java
│   │       ├── service
│   │       │   └── ExpenseService.java
│   │       └── ExpenseTrackerApiApplication.java
│   └── resources
│       └── application.properties
│
└── test
    └── java
        └── com.kajiru.expense_tracker_api
            ├── controller
            │   └── ExpenseControllerTest.java
            └── service
                └── ExpenseServiceTest.java
```

## Expense Model

Each expense contains:

| Field | Type | Description |
|---|---|---|
| `id` | `Long` | Unique database ID |
| `description` | `String` | Description of the expense |
| `amount` | `BigDecimal` | Expense amount |
| `category` | `String` | Expense category |
| `date` | `LocalDate` | Date of the expense |

## Validation

The API validates incoming expense data.

The following fields are required:

- Description must not be blank
- Amount must be greater than `0`
- Category must not be blank
- Date is required

Example invalid request:

```json
{
  "description": "",
  "amount": -5,
  "category": "",
  "date": null
}
```

Response:

```http
400 Bad Request
```

Example response:

```json
{
  "date": "Date is required",
  "amount": "Amount must be greater than 0",
  "description": "Description must not be blank",
  "category": "Category must not be blank"
}
```

## Exception Handling

The application uses a global exception handler with `@RestControllerAdvice`.

If an expense does not exist, the service throws a custom `ExpenseNotFoundException`.

Example:

```http
GET /api/expenses/999
```

Response:

```http
404 Not Found
```

```json
{
  "error": "Expense not found: 999"
}
```

## API Endpoints

Base URL:

```text
http://localhost:8080
```

### Create Expense

```http
POST /api/expenses
```

Request body:

```json
{
  "description": "Lebensmittel",
  "amount": 25.50,
  "category": "Food",
  "date": "2026-09-27"
}
```

Response:

```http
201 Created
```

Example:

```json
{
  "amount": 25.50,
  "category": "Food",
  "date": "2026-09-27",
  "description": "Lebensmittel",
  "id": 1
}
```

### Get All Expenses

```http
GET /api/expenses
```

Example response:

```json
[
  {
    "amount": 25.50,
    "category": "Food",
    "date": "2026-09-27",
    "description": "Lebensmittel",
    "id": 1
  }
]
```

### Get Expense by ID

```http
GET /api/expenses/{id}
```

Example:

```http
GET /api/expenses/1
```

### Update Expense

```http
PUT /api/expenses/{id}
```

Example:

```http
PUT /api/expenses/1
```

Request body:

```json
{
  "description": "Lebensmittel geändert",
  "amount": 30.00,
  "category": "Food",
  "date": "2026-09-27"
}
```

Response:

```http
200 OK
```

### Delete Expense

```http
DELETE /api/expenses/{id}
```

Example:

```http
DELETE /api/expenses/1
```

Response:

```http
204 No Content
```

## Database

The application uses PostgreSQL.

Create a database named:

```text
expensetrackerdb
```

Configure the database connection in:

```text
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/expensetrackerdb
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Replace `YOUR_PASSWORD` with your local PostgreSQL password.

Do not commit real database passwords or other credentials to a public repository.

## Running the Application

Clone the repository:

```bash
git clone https://github.com/Kajiru44/expense-tracker-api.git
```

Open the project in IntelliJ IDEA or another Java IDE.

Make sure PostgreSQL is running and the `expensetrackerdb` database exists.

Configure the database credentials in `application.properties`.

Start the application by running:

```text
ExpenseTrackerApiApplication
```

The API will be available at:

```text
http://localhost:8080
```

## Testing

The project contains unit tests for the service layer and controller tests using MockMvc.

### ExpenseServiceTest

The service tests cover:

- Creating an expense
- Getting all expenses
- Getting an expense by ID
- Handling a missing expense
- Updating an expense
- Deleting an expense

### ExpenseControllerTest

The controller tests cover:

- Creating an expense
- Getting all expenses
- Getting an expense by ID
- Handling a missing expense
- Updating an expense
- Deleting an expense
- Rejecting invalid input

All current tests pass successfully.

```text
ExpenseControllerTest
7 tests passed

ExpenseServiceTest
6 tests passed

Total
13 tests passed
```

## Architecture

The application follows a layered architecture:

```text
Client
  │
  ▼
Controller
  │
  ▼
Service
  │
  ▼
Repository
  │
  ▼
PostgreSQL
```

### Controller

Handles HTTP requests and responses.

### Service

Contains the application's business logic.

### Repository

Provides database access through Spring Data JPA.

### Model

The `Expense` entity represents an expense stored in the database.

### Exception Handling

Custom exceptions and `GlobalExceptionHandler` provide structured HTTP error responses.

## Example Request Flow

Creating an expense:

```text
POST /api/expenses
        │
        ▼
ExpenseController
        │
        ▼
Validation
        │
        ▼
ExpenseService
        │
        ▼
ExpenseRepository
        │
        ▼
PostgreSQL
```

Invalid input:

```text
POST /api/expenses
        │
        ▼
Validation fails
        │
        ▼
GlobalExceptionHandler
        │
        ▼
400 Bad Request
```

Missing expense:

```text
GET /api/expenses/999
        │
        ▼
ExpenseService
        │
        ▼
ExpenseNotFoundException
        │
        ▼
GlobalExceptionHandler
        │
        ▼
404 Not Found
```

## Project Status

The project currently provides the core functionality of a personal expense management REST API.

Implemented:

- REST API
- PostgreSQL persistence
- CRUD operations
- Input validation
- Global exception handling
- Custom 404 handling
- Service unit tests
- Controller tests
- 13 passing tests

The project is intentionally kept small and focused as a backend portfolio project.

## License

This project is for educational and portfolio purposes.
