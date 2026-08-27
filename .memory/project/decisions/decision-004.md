# Decision 004 - Global Exception Handling

## Date
2026-08-25

## Review by
2026-11-23

## Status
Active

## Decision
REST API exceptions will be handled centrally using Spring's `@ControllerAdvice`.

## Rationale
Centralized exception handling provides consistent JSON error responses across the REST API. It also prevents internal implementation details and stack traces from being exposed to clients.

## Alternatives rejected
Handling exceptions separately inside each controller was rejected because it would duplicate error-handling logic and produce inconsistent responses.

## The decision I want to record is
Common application and database exceptions, including `DataIntegrityViolationException`, must return an appropriate HTTP status and readable error message through the centralized exception handler.
