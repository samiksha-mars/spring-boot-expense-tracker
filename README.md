# Spring Boot Expense Tracker

A small REST API for tracking personal expenses, built with Spring Boot. Expenses are
persisted to an in-memory H2 database, and a summary endpoint reports totals by category.

## Features

- Create, read, update and delete expenses
- Filter expenses by category
- Summary endpoint with total spend and per-category breakdown
- Bean Validation (positive amounts, required fields)
- In-memory H2 database with console

## Tech Stack

- Java 17
- Spring Boot (Web MVC, Data JPA, Validation)
- H2 (in-memory database)

## Running

```bash
./mvnw spring-boot:run
```

App starts on http://localhost:8080. H2 console: http://localhost:8080/h2-console
(user `sa`, empty password). The database name is generated on each start, so copy the
JDBC URL from the startup log line `H2 console available at '/h2-console'. Database
available at 'jdbc:h2:mem:...'`.

## API

| Method | Path                     | Description                          |
|--------|--------------------------|--------------------------------------|
| GET    | `/api/expenses`          | List expenses (`?category=Food`)     |
| GET    | `/api/expenses/summary`  | Total and per-category breakdown     |
| GET    | `/api/expenses/{id}`     | Get a single expense                 |
| POST   | `/api/expenses`          | Create an expense                    |
| PUT    | `/api/expenses/{id}`     | Update an expense                    |
| DELETE | `/api/expenses/{id}`     | Delete an expense                    |

### Example

```bash
curl -X POST http://localhost:8080/api/expenses \
  -H "Content-Type: application/json" \
  -d '{"description": "Groceries", "amount": 42.50, "category": "Food", "date": "2026-11-24"}'
```

`amount` must be positive with at most 8 integer digits and 2 decimal places; `date` uses
`yyyy-MM-dd`; `category` is optional (expenses without one are summarised as
`uncategorized`).

### Errors

Errors are returned as RFC 7807 `ProblemDetail` JSON:

- `400` for invalid input, with an `errors` object mapping each rejected field to its message
- `404` when no expense has the given id
