# Memory Architecture

This document defines the architecture, scope, writing permissions, and retention rules for managing project memory, domain knowledge, reference documentation, and system data.

---

## 1. Project Memory

* **Location:** `.memory/project/`
* **Stores:** Current tasks, open issues, temporary decisions, and active project state.
* **Scope:** Project-specific; applies only to this repository.
* **Write Permissions:** Agent can create and update entries.
* **Pruning & Retention:**
  * Feature/branch entries are reviewed after pull request merge and archived when no longer needed.
  * Project-wide entries are reviewed when the decision or project priority changes.


---

## 2. Knowledge

* **Location:** `.memory/knowledge/`
* **Stores:** Stable banking, security, authorization, and testing rules.
* **Scope:** Project-specific and useful across multiple tasks/initiatives.
* **Write Permissions:** Human-controlled; agent has **read-only** access and must not change permissions. This prevents the agent from silently changing stable project rules.
* **Pruning & Retention:** Reviewed when a project-wide rule changes or becomes outdated.

---

## 3. Reference Documents

* **Location:** `.memory/reference/`
* **Stores:** Detailed API specifications, database schemas, workflow diagrams, and architecture documentation.
* **Scope:** Project-specific technical references.
* **Write Permissions:** Human-controlled.
* **Pruning & Retention:** Updated or removed when the referenced information changes.
* **Current State:** No reference documents are currently required. This layer will be populated when the project has large technical documents that are needed only for specific tasks.

---

## 4. What Does Not Belong in Memory

To maintain security, performance, and clear boundaries, the following data types must **never** be stored in memory directories:

| Data Type | Proper Target Location |
| :--- | :--- |
| **Source code & Automated tests** | Repository (`/src`, `/tests`) |
| **Customer, account, & transaction data** | Database |
| **Passwords, API keys, & secret tokens** | Secret Manager |
| **Step-by-step procedures** | Claude skills |
| **Temporary session details** | Current session only |

---

## 5. Allocation Rule

Use the matrix below to determine the correct location for any piece of information:

| Information Type | Target Location                            |
| :--- |:-------------------------------------------|
| **Current work** | Project Memory (`.memory/project/`)        |
| **Stable rules** | Knowledge (`.memory/knowledge/`)           |
| **Detailed information** | Reference Documents (`.memory/reference/`) |
| **Code and tests** | Repository (`/src`, `/tests`)              |
| **Customer data** | Database                                   |
| **Secrets** | Secret Manager                             |

---

## 6. Allocation Example

A deferred feature decision belongs in Project Memory because it may change.

A stable authorization rule belongs in Knowledge because it remains active until a human changes it.

A large API specification belongs in References because it is only needed for specific tasks.

---

> **Key Takeaway:** This separation keeps temporary project information, stable rules, detailed references, source code, and sensitive operational data strictly in their designated locations.