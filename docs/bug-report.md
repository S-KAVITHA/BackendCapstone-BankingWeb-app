# Bug Report

## Title
Bank transfers allow a negative (or zero) amount

## Severity
High — allows moving money in the wrong direction between accounts, or creating a no-op transfer record with a zero amount.

## Component
Fund transfer endpoint (`CustomerController` / `CustomerService`)

## Affected Files
- `src/main/java/com/example/demo/controller/CustomerController.java` — `POST /Customer/transferAccounts` (lines 102-107)
- `src/main/java/com/example/demo/service/CustomerService.java` — `transferFunds(FundsTransfer)` (lines 111-131)
- `src/main/java/com/example/demo/model/FundsTransfer.java` — `amount` field (line 60), no validation constraints

## Description
`CustomerService.transferFunds` debits `fundstransfer.getAmount()` from the source account and credits the same amount to the destination account with no check on the sign or value of `amount`:

```java
public FundsTransfer transferFunds(FundsTransfer fundstransfer) {
    fundstransfer.setCreatedDate(LocalDate.now());

    Optional<Account> fetchFAcct = accountRepository.findById(fundstransfer.getFromAcctNo());
    if (fetchFAcct.isPresent()) {
        Account fromAcct = fetchFAcct.get();
        fromAcct.setBalance(fromAcct.getBalance() - fundstransfer.getAmount());
        accountRepository.save(fromAcct);
    }

    Optional<Account> fetchTAcct = accountRepository.findById(fundstransfer.getToAcctNo());
    if (fetchTAcct.isPresent()) {
        Account toAcct = fetchTAcct.get();
        toAcct.setBalance(toAcct.getBalance() + fundstransfer.getAmount());
        accountRepository.save(toAcct);
    }

    return transferRepository.save(fundstransfer);
}
```

The controller passes the request body straight through with no `@Valid`/validation annotations, and the `FundsTransfer` entity has no constraints on `amount`. There is also no `@ControllerAdvice`/exception handling anywhere in the app to reject bad input.

## Steps to Reproduce
1. `POST /Customer/transferAccounts` with a body where `amount` is negative, e.g. `-100`, for a valid `fromAcctNo`/`toAcctNo` pair.
2. Observe the response: HTTP 200 with the `FundsTransfer` echoed back.
3. Check account balances: the "from" account's balance increases and the "to" account's balance decreases — the transfer runs in reverse.
4. Repeat with `amount = 0`: the request succeeds and a no-op transfer record is persisted.

## Expected Behavior
Any transfer request with `amount <= 0` must be rejected (no balance changes, no `FundsTransfer` record persisted) with an appropriate error response (e.g. HTTP 400 with a readable message).

## Actual Behavior
Any `amount` value, including negative and zero, is accepted and applied to both account balances without error.

## Root Cause
No validation exists on the transfer amount in either the controller or the service layer before the debit/credit is applied.

## Suggested Fix
Validate `amount > 0` in `CustomerService.transferFunds` before mutating any account balance, and reject invalid requests with a clear error (HTTP 400). Since the codebase has no existing exception-handling mechanism, this is a good place to introduce the centralized `@ControllerAdvice` handler already called for by project decision-004 (`.memory/project/decisions/decision-004.md`).

## Related Notes
- `depositAccount` and `withdrawAccount` in the same service have the identical gap (no amount validation, no insufficient-funds check) but are out of scope for this report, which is limited to the transfer endpoint.
- No existing automated test covers negative/zero-amount transfers (`CustomerServiceTest.testTransferFunds_Success` and `CustomerControllerTest.testTransferAccounts` only exercise the happy path).
