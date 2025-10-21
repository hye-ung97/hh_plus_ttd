# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **TDD-based point management system** for the hhplus week 1 assignment. The project is a Spring Boot 3.2 application using Java 17 and Gradle, focused on implementing user point operations (inquiry, charge, use, and history tracking) following Test-Driven Development practices.

## Build and Test Commands

### Run Tests
```bash
./gradlew test
```

### Run a Single Test Class
```bash
./gradlew test --tests "ClassName"
```

### Run a Single Test Method
```bash
./gradlew test --tests "ClassName.methodName"
```

### Build the Application
```bash
./gradlew bootJar
```

### Run the Application
```bash
./gradlew bootRun
```

### Code Coverage Report (Jacoco)
```bash
./gradlew jacocoTestReport
# Report available at: build/reports/jacoco/test/html/index.html
```

## Architecture

### Package Structure
```
io.hhplus.tdd/
├── database/          # Data access layer (in-memory tables)
│   ├── UserPointTable.java       # User point storage with throttling
│   └── PointHistoryTable.java    # Transaction history storage
├── point/             # Point domain logic
│   ├── PointController.java      # REST API endpoints
│   ├── UserPoint.java            # Point entity (record)
│   ├── PointHistory.java         # History entity (record)
│   └── TransactionType.java      # CHARGE/USE enum
├── ApiControllerAdvice.java      # Global exception handler
└── TddApplication.java           # Spring Boot entry point
```

### Data Layer Design

The `database` package contains **in-memory table implementations** that simulate database operations with artificial latency:

- **UserPointTable**: HashMap-based storage with 200ms random throttle on reads, 300ms on writes
- **PointHistoryTable**: ArrayList-based storage with 300ms random throttle on inserts
- **Important**: These table classes should NOT be modified. Use only their public APIs for data operations.

### Domain Models

- **UserPoint**: Record with `id`, `point` (balance), `updateMillis` (timestamp)
- **PointHistory**: Record with `id`, `userId`, `amount`, `type` (CHARGE/USE), `updateMillis`

Both use Java records for immutability.

### Current Implementation Status

The controller endpoints are **stub implementations** that need to be developed following TDD:
1. `GET /point/{id}` - Query user point balance
2. `GET /point/{id}/histories` - Query transaction history
3. `PATCH /point/{id}/charge` - Charge points (with amount in request body)
4. `PATCH /point/{id}/use` - Use points (with amount in request body)

## TDD Requirements

Follow the Red-Green-Refactor cycle:
1. Write failing tests first
2. Implement minimum code to pass tests
3. Refactor for clean code

### Key Testing Requirements
- Unit tests for all point operations (inquiry, charge, use, history)
- Exception case testing (insufficient balance, invalid amounts, etc.)
- Integration tests for complete flows
- **Concurrency control testing** - Critical for preventing race conditions during simultaneous point operations
- Document concurrency control approach in README.md

### Concurrency Considerations

Given the throttled in-memory tables, implement concurrency control to handle:
- Multiple simultaneous charges/uses for the same user
- Race conditions in point balance updates
- Transaction history consistency

Common approaches: optimistic locking, pessimistic locking, or synchronized methods.

## PR Guidelines

When creating pull requests, follow the template in `.github/pull_request_template.md`:
- Title format: `[STEP0X] Name`
- Include checklist covering TDD basics, advanced TDD, and AI usage
- Link relevant commits
- Add review points and brief retrospective (3 lines max)

## Development Dependencies

Key libraries (defined in `gradle/libs.versions.toml`):
- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- JUnit 5.9.3
- AssertJ 3.24.2
- Lombok 1.18.22
- TestContainers 1.19.3 (available but not required for in-memory implementation)
- Fixture Monkey 1.0.13 (for test data generation)

## Notes

- Java source compatibility: Java 17
- Test failures are ignored in build configuration (`ignoreFailures = true`)
- JaCoCo version: 0.8.7
- Spring Boot uses Lombok for boilerplate reduction (ensure annotation processing is enabled in IDE)
