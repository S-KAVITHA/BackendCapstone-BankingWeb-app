# Lint Report v2 — Checkstyle (Sun Checks)

**Date:** 2026-11-07
**Project:** BankingProject_Backend

## Command

```
mvn org.apache.maven.plugins:maven-checkstyle-plugin:3.3.1:checkstyle -Dcheckstyle.config.location=sun_checks.xml
```

## Result

| Item | Value |
|---|---|
| Exit status | **0** (`BUILD SUCCESS`) |
| Checkstyle engine | Checkstyle 9.3 (via maven-checkstyle-plugin 3.3.1) |
| Ruleset | `sun_checks.xml` |
| Files scanned | 18 |
| Files with violations | 18 |
| **Total violations** | **982** |
| Severity `error` | 982 |
| Severity `warning` | 0 |
| Severity `info` | 0 |

Maven reported: `There are 982 errors reported by Checkstyle 9.3 with sun_checks.xml ruleset.`

The exit status is 0 even with 982 errors because the `checkstyle:checkstyle` goal only generates a report. It does not fail the build. To fail the build on violations, use `checkstyle:check`.

Output artifacts: `target/checkstyle-result.xml`, `target/site/checkstyle.html`.

## Maven build warnings (not Checkstyle violations)

```
[WARNING] Unable to locate Source XRef to link to - DISABLED
[WARNING] Unable to locate Test Source XRef to link to - DISABLED
```

No `[ERROR]` lines appeared in the Maven output.

## Violations by category (Checkstyle rule)

| Rule | Count |
|---|---|
| MissingJavadocMethod | 206 |
| DesignForExtension | 190 |
| FinalParameters | 189 |
| HiddenField | 128 |
| JavadocVariable | 78 |
| LineLength | 68 |
| RegexpSingleline (trailing whitespace) | 41 |
| UnusedImports | 37 |
| FileTabCharacter | 16 |
| AvoidStarImport | 8 |
| NewlineAtEndOfFile | 6 |
| JavadocPackage | 5 |
| ParameterNumber | 5 |
| MagicNumber | 2 |
| MemberName | 1 |
| GenericWhitespace | 1 |
| WhitespaceAround | 1 |
| **Total** | **982** |

## Violations by file

| File | Count |
|---|---|
| src/main/java/com/example/demo/model/Customer.java | 172 |
| src/main/java/com/example/demo/model/FundsTransfer.java | 133 |
| src/main/java/com/example/demo/model/Transaction.java | 114 |
| src/main/java/com/example/demo/model/ChequeRequest.java | 107 |
| src/main/java/com/example/demo/model/Account.java | 103 |
| src/main/java/com/example/demo/model/BankAdmin.java | 60 |
| src/main/java/com/example/demo/service/CustomerService.java | 60 |
| src/main/java/com/example/demo/controller/AdminController.java | 56 |
| src/main/java/com/example/demo/controller/CustomerController.java | 54 |
| src/main/java/com/example/demo/service/AdminService.java | 52 |
| src/main/java/com/example/demo/BankingProjectBackendApplication.java | 20 |
| src/main/java/com/example/demo/repository/CustomerRepository.java | 18 |
| src/main/java/com/example/demo/repository/AccountRepository.java | 11 |
| src/main/java/com/example/demo/repository/FundsTransferRepository.java | 8 |
| src/main/java/com/example/demo/repository/TransactionRepository.java | 7 |
| src/main/java/com/example/demo/repository/ChequeRequestRepository.java | 4 |
| src/main/java/com/example/demo/repository/BankAdminRepository.java | 2 |
| src/main/resources/application.properties | 1 |

## Notes

- Nothing was fixed. This is a record of the run only.
- Javadoc and extensibility rules (MissingJavadocMethod, DesignForExtension, JavadocVariable) and FinalParameters make up 663 of the 982 violations (about 68%).
