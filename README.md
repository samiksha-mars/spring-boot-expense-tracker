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
(JDBC URL `jdbc:h2:mem:testdb`, user `sa`, empty password).

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
