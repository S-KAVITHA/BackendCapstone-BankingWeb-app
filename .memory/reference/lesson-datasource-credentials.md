---
classification: confidential
project: proj-lessons
doc_type: lesson
---

# Lesson: Datasource credentials must not live in application.properties

## What happened
The application smoke test found that `src/main/resources/application.properties` hardcodes the datasource URL, username and password, and that the username is the database administrative account. Because the file is committed, the credentials are in the repository history and visible to every agent and tool that reads the source tree.

## What we learned
Credentials in a tracked configuration file are effectively published to everyone with repository access, including agents. Removing them from the latest commit does not remove them from history, so the password itself must be changed.

## How to apply it
Replace the values with environment placeholders (`${DB_USERNAME}`, `${DB_PASSWORD}`) and pass them at `docker run`. Create a least-privilege application user instead of the administrative account. Rotate the existing password. Never copy credential values into memory, logs, transcripts or documentation, and keep this lesson out of agent retrieval: it is classified confidential, so an internal-ceiling retrieval must not return it.
