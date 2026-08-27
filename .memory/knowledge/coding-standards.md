# Coding Standards

Derived from the actual conventions in `src/main/java/com/example/demo`
as of 2026-08-25. This describes what the codebase currently does, not
an externally imposed style guide.

## Package layout

Code is organized by layer under `com.example.demo`:

- `controller/` — `@RestController` classes, one per resource (e.g.
  `CustomerController`, `AdminController`)
- `service/` — `@Service` classes holding business logic, one per
  resource (e.g. `CustomerService`, `AdminService`)
- `repository/` — `@Repository` interfaces extending Spring Data's
  `CrudRepository<Entity, Long>`, one per entity
- `model/` — `@Entity` classes (JPA entities), one per table

## Controllers

- Annotate with `@RestController` and a resource-level
  `@RequestMapping("ResourceName")` (capitalized, e.g. `"Customer"`).
- Endpoint methods use verb+noun names matching their HTTP mapping
  path, e.g. `@PostMapping("addCustomer")` → `addCustomer(...)`.
- Path variables use `@PathVariable`; request bodies use
  `@RequestBody`.
- Services are field-injected with `@Autowired` (no constructor
  injection currently in use).

## Services

- Annotate the class with `@Service` and `@Transactional`.
- Repositories are field-injected with `@Autowired`, one field per
  repository the service needs.
- Method names mirror the controller action that calls them
  (`addCustomer`, `getCustomers`, `depositAccount`, ...).
- Timestamps (`createdDate`) are set in the service layer via
  `LocalDate.now()` before saving, not left to the database or entity
  defaults.

## Repositories

- Interfaces extend `CrudRepository<Entity, Long>` (not
  `JpaRepository`) and are annotated `@Repository`.
- Simple lookups use Spring Data derived query methods (e.g.
  `findByEmailId`, `findByStatus`).
- Custom joins use `@Query` with JPQL and `@Param`-bound parameters.

## Entities / models

- Annotate with `@Entity`, `@Table(name = "...")`.
- Every field has an explicit `@Column(name = "...")`, even when the
  name matches the field.
- IDs use `@Id @GeneratedValue(strategy = GenerationType.AUTO,
  generator = "...")` paired with a named `@SequenceGenerator`.
- Both a no-arg constructor and an all-args constructor are written
  explicitly (no Lombok in use).
- Getters/setters are written explicitly for every field.
- Relationships use `@OneToMany` with explicit `mappedBy`, `fetch =
  FetchType.LAZY`, and `cascade = CascadeType.ALL`, and are marked
  `@JsonIgnore` to keep them out of serialized responses.

## Notes worth flagging (not necessarily standards to keep)

These are consistent patterns in the code, but they're worth a second
look rather than treating as things to replicate going forward:

- Logging is done via `System.out.println(...)` in controllers rather
  than a logging framework.
- `@CrossOrigin(origins = "*", allowedHeaders = "*")` allows any
  origin — fine for local dev, a real risk if this ever reaches a
  non-dev environment.
- `Customer.password` is a plain `String` field with no visible
  hashing/encryption applied before persistence.
- Dependency injection is field-based (`@Autowired` on fields) rather
  than constructor-based, which is harder to unit test.

## Ownership & Review

- Owner: Human project maintainer.
- The agent must treat this file as read-only.
- Review when project-wide coding conventions change or when the
  implementation no longer matches these standards.
- Security risks listed under "Notes worth flagging" should be reviewed
  before related code is changed.
