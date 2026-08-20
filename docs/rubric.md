# Evaluation Rubric

## 1. Task Completion

**Definition:** Measures whether the agent completed the assigned validation task according to the PRD acceptance criteria.

### Score 1 - Does not meet
The agent did not complete the requested validation task.

**Example:** The agent failed to run the requested Maven validation command.

### Score 2 - Partially meets
The agent started the validation but did not provide complete results.

**Example:** Checkstyle was started, but the agent did not report the total violations or other required results.

### Score 3 - Meets
The agent completed the validation and provided the expected output.

**Example:** Maven tests or Checkstyle were executed and the required results were documented.

### Score 4 - Exceeds
The agent completed the validation and provided additional useful analysis beyond the required results.

**Example:** The agent completed the validation, documented the results, and explained important findings.

---

## 2. Result Accuracy

**Definition:** Measures whether the reported results accurately match the validation command output and provide objective evidence.

### Score 1 - Does not meet
The reported result contradicts the actual command output.

**Example:** The agent reports that validation passed when the command returned a non-zero exit status.

### Score 2 - Partially meets
Some results are correct, but important evidence is missing or inaccurate.

**Example:** The agent reports Checkstyle violations but provides an incorrect total or omits important results.

### Score 3 - Meets
The documented result accurately reflects the command output.

**Example:** The agent correctly reports the Maven exit status, test counts, or Checkstyle violations.

### Score 4 - Exceeds
The results are accurate and include useful explanation of important or unexpected findings.

**Example:** The agent accurately reports Checkstyle violations and explains the main categories or significant findings.

---

## 3. Scope Compliance

**Definition:** Measures whether the agent performs only the actions required by the assigned validation task and avoids unrelated project changes.

### Score 1 - Does not meet
The agent modifies unrelated files or performs unauthorized actions.

**Example:** The agent changes application source code while performing validation.

### Score 2 - Partially meets
The agent mostly follows the task scope but makes unnecessary changes.

**Example:** The agent creates unrelated files during validation.

### Score 3 - Meets
The agent stays within the assigned validation scope.

**Example:** The agent runs the requested Maven validation without modifying unrelated project files.

### Score 4 - Exceeds
The agent follows the scope and provides additional verification that no unexpected changes occurred.

**Example:** The agent runs the validation and verifies the Git working tree for unexpected changes.

---

## Pass Threshold

A run passes when:

- All three rubric dimensions score **3 or higher**.
- The required validation task is completed.
- Results are documented with objective evidence from the command output.
- The agent does not make unauthorized or unrelated project changes.
