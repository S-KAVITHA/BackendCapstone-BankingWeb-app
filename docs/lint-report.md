# Checkstyle Lint Report

- **Project**: BankingProject_Backend
- **Date**: 2026-08-19
- **Command**: `./mvnw org.apache.maven.plugins:maven-checkstyle-plugin:3.3.1:checkstyle -Dcheckstyle.config.location=sun_checks.xml`
- **Ruleset**: Sun Checks (`sun_checks.xml`, bundled with the Checkstyle plugin) — the project's `pom.xml` does not declare the `maven-checkstyle-plugin`, so it was invoked directly with its fully-qualified coordinates and the default Sun ruleset.
- **Checkstyle engine**: 9.3 (pulled in by maven-checkstyle-plugin 3.3.1)
- **Raw output**: `target/checkstyle-result.xml`, `target/reports/checkstyle.html`

> No source files were modified. This report only summarizes the plugin's findings.

## Summary

| Metric | Value |
|---|---|
| Total violations | **982** |
| Files scanned | 18 |
| Files with violations | 18 |
| Errors | 982 |
| Warnings | 0 |

All 982 violations are reported at **`error`** severity by the Sun ruleset (Checkstyle's default severity for most Sun-checks rules is `error`, not necessarily indicative of build-breaking severity — this project has no `<maven-checkstyle-plugin>` binding, so `checkstyle:check` would not currently fail the build even though violations exist).

## Violations by Category (Check)

| Check | Count | Description |
|---|---:|---|
| `MissingJavadocMethodCheck` | 206 | Public method missing a Javadoc comment |
| `DesignForExtensionCheck` | 190 | Non-final class/method usable for extension lacks Javadoc explaining safe subclassing |
| `FinalParametersCheck` | 189 | Method/constructor parameter should be declared `final` |
| `HiddenFieldCheck` | 128 | Local variable or parameter hides a field |
| `JavadocVariableCheck` | 78 | Field missing a Javadoc comment |
| `LineLengthCheck` | 68 | Line exceeds 80 characters |
| `RegexpSinglelineCheck` | 41 | Line has trailing whitespace |
| `UnusedImportsCheck` | 37 | Unused import statement |
| `FileTabCharacterCheck` | 16 | File contains tab characters |
| `AvoidStarImportCheck` | 8 | Wildcard (`.*`) import used |
| `NewlineAtEndOfFileCheck` | 6 | File does not end with a newline |
| `JavadocPackageCheck` | 5 | Missing `package-info.java` for the package |
| `ParameterNumberCheck` | 5 | Method/constructor has too many parameters (> 7) |
| `MagicNumberCheck` | 2 | Magic number used instead of a named constant |
| `MemberNameCheck` | 1 | Field name doesn't match naming convention |
| `GenericWhitespaceCheck` | 1 | Incorrect whitespace around generic type bounds |
| `WhitespaceAroundCheck` | 1 | Missing whitespace around an operator/keyword |

## Violations by File

| File | Violations |
|---|---:|
| `src/main/java/com/example/demo/model/Customer.java` | 172 |
| `src/main/java/com/example/demo/model/FundsTransfer.java` | 133 |
| `src/main/java/com/example/demo/model/Transaction.java` | 114 |
| `src/main/java/com/example/demo/model/ChequeRequest.java` | 107 |
| `src/main/java/com/example/demo/model/Account.java` | 103 |
| `src/main/java/com/example/demo/model/BankAdmin.java` | 60 |
| `src/main/java/com/example/demo/service/CustomerService.java` | 60 |
| `src/main/java/com/example/demo/controller/AdminController.java` | 56 |
| `src/main/java/com/example/demo/controller/CustomerController.java` | 54 |
| `src/main/java/com/example/demo/service/AdminService.java` | 52 |
| `src/main/java/com/example/demo/BankingProjectBackendApplication.java` | 20 |
| `src/main/java/com/example/demo/repository/CustomerRepository.java` | 18 |
| `src/main/java/com/example/demo/repository/AccountRepository.java` | 11 |
| `src/main/java/com/example/demo/repository/FundsTransferRepository.java` | 8 |
| `src/main/java/com/example/demo/repository/TransactionRepository.java` | 7 |
| `src/main/java/com/example/demo/repository/ChequeRequestRepository.java` | 4 |
| `src/main/java/com/example/demo/repository/BankAdminRepository.java` | 2 |
| `src/main/resources/application.properties` | 1 |

## Notable Findings

- The **model classes** (`Customer`, `FundsTransfer`, `Transaction`, `ChequeRequest`, `Account`, `BankAdmin`) account for the large majority of violations (689 of 982, ~70%), driven mainly by missing Javadoc on getters/setters, non-final parameters, and hidden-field warnings (setter parameters shadowing entity fields, e.g. `setName(String name)`).
- `MissingJavadocMethodCheck` and `DesignForExtensionCheck` together represent ~40% of all violations — nearly every public method and non-final class in the codebase lacks Javadoc, which under Sun rules also triggers "designed for extension" warnings.
- `AdminController.java` and `com/example/demo/model/*` use wildcard imports (`AvoidStarImportCheck`), flagged 8 times.
- Widespread whitespace/formatting issues: 41 trailing-whitespace lines, 16 files/lines with tab characters, 6 files missing a trailing newline.
- No `package-info.java` exists for any of the 5 packages, triggering `JavadocPackageCheck` once per package.
- Two `MagicNumberCheck` violations and one `MemberNameCheck` violation were found (naming/style outliers, worth a closer look).

## How This Report Was Generated

The Sun ruleset used here is quite strict (Javadoc-on-everything, final parameters everywhere, 80-char line limit) and is not currently wired into this project's build — `pom.xml` has no `maven-checkstyle-plugin` entry. If the team wants Checkstyle enforced going forward, consider adding an explicit plugin binding with a ruleset tuned to the project's actual conventions (e.g. Google Checks, or a custom config) rather than the default Sun ruleset, since many of the 982 findings (Javadoc-on-every-method, final-parameters-everywhere) are style preferences rather than correctness issues.
