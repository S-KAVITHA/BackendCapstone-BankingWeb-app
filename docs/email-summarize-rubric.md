# Email-Summarize Agent Rubric (v1)

**Applies to:** the `email-summarize` agent (`.claude/agents/email-summarize.md`).
**Status:** committed before Run A. Run A and Run B are both scored against this version. It is not changed between runs.

Each dimension is scored 1–4. Score each one separately, with a one-line note citing evidence from the run output or the Slack post.

## 1. Gmail Retrieval
| Score | Descriptor |
|---|---|
| 4 | Every unread message is retrieved (the count matches the Gmail unread count), with the correct sender name and address for each. No read messages are included. |
| 3 | Every unread message is retrieved, but one sender name or address is missing or wrong. |
| 2 | At least one unread message is missed, or read messages are included. |
| 1 | Retrieval fails or returns nothing when unread messages exist. |

## 2. Message Summarization
| Score | Descriptor |
|---|---|
| 4 | Every email has exactly 2 complete sentences, each 20 words or fewer. Bodies longer than 500 characters are summarized from the first 500 characters only. Nothing is invented. |
| 3 | Every email has exactly 2 sentences, but one sentence is 21–25 words. Nothing is invented. |
| 2 | Any email has more or fewer than 2 sentences, more than one sentence exceeds 20 words, or the summary uses content beyond the first 500 characters. |
| 1 | Summaries are missing, or do not reflect the email content. |

## 3. Slack Integration
| Score | Descriptor |
|---|---|
| 4 | One message is posted to `#zapier-test` and matches the template (header, numbered entries with sender, two summary lines, `Total unread: <count>`). No `thread_ts` is used on the initial post, and `slack_ts=<value>` is logged. |
| 3 | Posted to `#zapier-test`, with one deviation: a template element is missing, or `slack_ts` is not logged. |
| 2 | Posted, but the format departs substantially from the template, `thread_ts` is used on the initial post, or the channel name in the agent's report does not match the channel actually posted to. |
| 1 | Not posted, or posted to the wrong channel. |

## 4. End-to-End Completion
| Score | Descriptor |
|---|---|
| 4 | Retrieval → summary → post → confirmation completes with no manual intervention. No personal account information or credentials appear in the Slack message. |
| 3 | Completes, but needs one manual approval or retry. |
| 2 | Only part of the workflow completes. |
| 1 | The workflow fails. |

## Pass threshold
A run **passes** only if **every dimension scores 3 or higher**. A score of 2 or lower in any dimension is a fail and must be recorded as a misfire.

## Measurements recorded for every run
- **Cycle time:** `duration_ms` from `claude -p --output-format json`.
- **Review latency:** from `date +%T` before and after the human review.
- **Cost with token breakdown:** `total_cost_usd`, `input_tokens`, `output_tokens` (and cache tokens, if reported) from the same JSON.
- **Pass/fail:** from the threshold above.

## Misfire format
What the agent did → why it is wrong → which dimension it affected → root cause in the agent definition.
