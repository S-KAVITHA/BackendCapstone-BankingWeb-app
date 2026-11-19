---
classification: internal
project: proj-lessons
doc_type: lesson
---

# Lesson: Validate both accounts before moving money in transferFunds

## What happened
During the Run 028 task-resumption check, the agent read `CustomerService.transferFunds` and found that it debits the source account and saves it before it looks up the target account. If the target account number does not exist, the credit branch is skipped, no exception is raised, and the transfer record is still saved. The money leaves the source account and arrives nowhere.

## What we learned
Every money-movement path must check all preconditions before the first write: both accounts exist, the amount is positive, and the source balance covers it. The class is `@Transactional`, but that only helps if a failure throws. A silent `if (isPresent())` skip commits the partial update.

## How to apply it
Before changing any deposit, withdrawal or transfer method, load both accounts first and throw a not-found or validation exception if either is missing or the amount is invalid. Only then debit and credit. Route the exception through the central `@ControllerAdvice` handler (decision-004) so the client gets a clear error instead of a 200 response. Add a unit test under `src/test/java` for a missing target account.
