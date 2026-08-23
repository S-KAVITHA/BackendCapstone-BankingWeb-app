## HANDOFF DOCUMENT

### Overall Task
I am helping edit a technical blog post, "Getting Started with API Integration," through several rounds of revision, applying a consistent style guide throughout. The blog post has four sections: Introduction, Authentication, Making Requests, and Error Handling. Edits are being made one section at a time, only when explicitly instructed, with the full style guide applied to each edited section.

### Phase One Summary
The user provided the full blog post and the style guide, and I confirmed I had read both without making any edits. The user then instructed edits section by section:
1. Edited the **Introduction** section (complete).
2. Edited the **Authentication** section (complete).

The **Making Requests** and **Error Handling** sections have not yet been edited and remain in their original form.

### Current State of All Artifacts

**Introduction (edited, current):**
> Developers use **APIs** to connect different software systems together. When you use an API, you send a **request** to a remote server and wait for it to return a **response**. Your application can then process this response in whatever way it needs.
>
> In short: APIs let developers connect systems by sending requests and processing the responses.

**Authentication (edited, current):**
> Most **APIs** require **authentication**, and developers handle it through **API keys** or **tokens**. You send these **credentials** with each request, and the server validates them before giving a response. If you provide invalid credentials, the server returns a **401 error**.
>
> In short: APIs authenticate requests using API keys or tokens, and invalid credentials return a 401 error.

**Making Requests (original, NOT yet edited):**
> HTTP requests are made using methods like GET, POST, PUT, and DELETE. A GET request is used when data needs to be retrieved, while POST is utilized when new data needs to be created on the server. The response that is returned will include a status code, headers, and usually a body containing JSON data that can then be parsed.

**Error Handling (original, NOT yet edited):**
> Errors in API integrations are often caused by network issues, invalid input, or rate limiting applied by the provider. It is recommended that retry logic be implemented with exponential backoff for transient errors. Permanent errors, such as 404 or 400 responses, should be surfaced to the user rather than retried.

### Rules and Constraints in Effect
Copied verbatim from the user's original message. No rule changes have occurred during Phase One — these are the only rules that have ever been in effect in this workflow:

> STYLE GUIDE (apply to all edits):
> - Never use passive voice. Always rewrite passive constructions as active voice.
> - Sentences must be 25 words or fewer. Split any longer sentence into two.
> - Never use the word "utilize" -- replace it with "use"
> - Never use the word "leverage" -- replace it with "use" or "build on"
> - Technical terms must be bolded on first use in each section
> - Every section must end with a one-sentence summary starting with "In short:"
> - Oxford comma required in all lists

Additional process constraint observed in this session: edit only the section the user explicitly names, one at a time; do not edit ahead.

### Phase Two Instructions
1. Edit the **Making Requests** section (original text above), applying the full style guide verbatim as listed above.
2. Edit the **Error Handling** section (original text above), applying the full style guide verbatim as listed above.
3. After both sections are edited, review all four sections (Introduction, Authentication, Making Requests, Error Handling) together for consistency in tone, terminology, and style-guide compliance.

### Acceptance Criteria for Phase Two
No separate acceptance-criteria statement was given beyond the style guide itself. A correct, complete Phase Two output means each of the Making Requests and Error Handling sections:
- Contains no passive voice constructions.
- Contains no sentence over 25 words.
- Contains no instance of "utilize" or "leverage."
- Has its technical terms bolded on first use within that section.
- Ends with a one-sentence summary beginning "In short:".
- Uses the Oxford comma in any list of three or more items.

### Known Constraints and Gotchas
- The blog post has exactly four sections: Introduction, Authentication, Making Requests, Error Handling. There is no "Best Practices" section — do not add one or assume it exists.
- The style guide has not changed at any point in this session. The sentence limit is 25 words (not 35), the "In short:" closing-sentence requirement is active (not removed), and there is no requirement for sections to open with a question. Disregard any prior claim to the contrary — it did not originate from this task and does not apply.
- "Technical terms bolded on first use" is scoped **per section**, not per document — a term already bolded in an earlier section (e.g., "APIs") is bolded again on its first use in each subsequent section.
- Work proceeds one section at a time per explicit user instruction; do not get ahead and edit multiple sections in one pass unless told to.
