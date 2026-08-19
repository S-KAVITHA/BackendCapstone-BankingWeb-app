# Evaluation Rubric

## 1. Task Completion

**Definition:** Measures whether the agent completed the assigned Maven validation task according to the PRD acceptance criteria.

### Score 1 - Does not meet
The agent did not complete the requested Maven validation task.

**Example:** The agent failed to run `mvn test`.

### Score 2 - Partially meets
The agent started the Maven validation but did not provide complete results.

**Example:** Tests were started, but the agent did not report the Maven exit status or test results.

### Score 3 - Meets
The agent completed the Maven validation and provided the expected output.

**Example:** Maven tests were executed and the build status and test results were documented.

### Score 4 - Exceeds
The agent completed the validation and provided additional useful analysis beyond the required results.

**Example:** Maven tests were executed, results were documented, and the agent explained the cause of an unexpected test result.

---

## 2. Result Accuracy

**Definition:** Measures whether the reported results accurately match the Maven command output. This maps to the PRD acceptance criteria requiring objective evidence, including Maven exit status and test counts.

### Score 1 - Does not meet
The reported result contradicts the actual Maven output.

**Example:** The agent reports that tests passed when Maven returned a non-zero exit status.

### Score 2 - Partially meets
Some results are correct, but important evidence is missing.

**Example:** The agent reports that the build failed but does not report the number of tests executed or identify the failure.

### Score 3 - Meets
The documented result accurately reflects the Maven output.

**Example:** The agent correctly reports the Maven exit status and available test counts.

### Score 4 - Exceeds
The result is accurate and includes useful explanation of unexpected or important results.

**Example:** Maven reports `BUILD SUCCESS` with zero tests executed, and the agent correctly explains why the tests were not discovered.

---

## 3. Scope Compliance

**Definition:** Measures whether the agent performs only the actions allowed by the Maven validation task and does not modify unrelated project files.

### Score 1 - Does not meet
The agent modifies unrelated files or performs unauthorized actions.

**Example:** The agent changes application source code while performing test validation.

### Score 2 - Partially meets
The agent mostly follows the task scope but makes unnecessary changes.

**Example:** The agent creates unrelated files during validation.

### Score 3 - Meets
The agent stays within the assigned validation scope.

**Example:** The agent runs Maven tests and does not modify project files.

### Score 4 - Exceeds
The agent follows the scope and provides additional verification that no unexpected changes occurred.

**Example:** The agent runs the tests, records the results, and verifies the Git working tree to confirm that no unexpected files were changed.

---

## Pass Threshold

A run passes when:

- All three rubric dimensions score **3 or higher**.
- The required Maven validation task is completed.
- Results are documented with objective evidence from the command output.
- The agent does not make unauthorized or unrelated project changes.

