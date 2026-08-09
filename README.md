# Payment Flow

## How to run

### Option 1 - Docker Compose (everything, no local setup)

Builds the application image and starts it together with PostgreSQL:

```bash
docker compose up --build
```

The API is then available at `http://localhost:8080/v1`, and PostgreSQL is exposed on `localhost:5432`.

To stop, and to also drop the database volume:

```bash
docker compose down -v
```

Note: the compose file sets `SPRING_JPA_HIBERNATE_DDL_AUTO=update`, so Hibernate creates the schema on
first start and keeps it in sync afterwards - the database itself starts empty and the repo carries no
migrations. Data survives restarts in the `postgres-data` volume.

### Option 2 - Build the jar and run it

```bash
./mvnw clean package
```

```bash
java -jar target/payment-flow-0.0.1-SNAPSHOT.jar
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

This expects a PostgreSQL instance to already be reachable with the settings from
`src/main/resources/application.yaml` (`jdbc:postgresql://localhost:5432/flowdb`, user `postgres`,
password `123`) - `docker compose up -d postgres` provides one. Override with the standard Spring
environment variables (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`,
`SPRING_DATASOURCE_PASSWORD`) if needed. Because `ddl-auto` is `validate`, the schema must already
exist; on a fresh database, create it on the first run:

```bash
java -jar target/payment-flow-0.0.1-SNAPSHOT.jar --spring.jpa.hibernate.ddl-auto=create
```

### Tests

```bash
./mvnw test
```

Unit tests run standalone. The integration test starts a real PostgreSQL container through
Testcontainers, so **Docker must be running** for it to pass.

---

## Architecture decisions

### 1. Layered architecture with three separate representations of a payment

The code is split into `controller` → `service` → `repository`, and a payment is modelled three times
on purpose:

- `domain.request` / `domain.reponse` - the **API contract** (`PaymentEventRequest`, `PaymentResponse`,
  `PaymentStatusResponse`, `PaymentsPageResponse`)
- `domain.model.Payment` - the **domain model** the service works with
- `domain.entity.PaymentEntity` - the **persistence model** mapped to the `payment` table

This keeps the three concerns free to evolve independently: a column
rename does not leak into the JSON contract, and the API can expose formatted values (dates and status
as strings in `PaymentResponse`) without forcing the domain to carry presentation types. The service
layer never sees a request or response object - the controller maps at the boundary.

**MapStruct** generates the conversions at compile time, so there is no reflection cost and a mapping
that no longer type-checks becomes a build failure instead of a runtime surprise.

### 2. Status progression encoded as a rank on the enum

`PaymentStatus` carries an `order` field rather than relying on the declaration order:

```
PENDING(0) → PROCESSING(1) → SETTLED(100) / REJECTED(100)
```

Comparing ranks - instead of hardcoding a transition table - keeps the out-of-order check to a single
comparison. `SETTLED` and `REJECTED` deliberately share the same rank because both are **terminal**:
equal rank means neither can override the other, so a payment that has reached a final state stays
there. The gap between `1` and `100` leaves room to insert intermediate states later without
renumbering the terminal ones.

### 3. Reference id, not the database id, is the business key

Clients only ever address payments by `referenceId` (the provider's identifier). The numeric `id` is an
internal surrogate key and is never exposed - the request maps it as ignored and no response type
carries it. This keeps the API stable regardless of how rows are stored, and it is what makes the
dedup lookup in ingestion meaningful.

### 4. Centralised error handling

`GlobalExceptionHandler` (`@RestControllerAdvice`) turns `PaymentNotFoundException` into a `404` with a
consistent `ErrorResponse` body (`status`, `message`, `timestamp`, `path`). Services throw domain
exceptions and stay free of HTTP concerns; the shape of an error is defined in exactly one place.

### 5. Pagination delegated to Spring Data, translated at the edge

The repository is a plain `JpaRepository`, so paging is `Pageable` all the way down and the database
does the work. The one adaptation is that the API is **1-based** while Spring Data is 0-based: the
controller subtracts one on the way in and the mapper adds one on the way out, so the off-by-one lives
in exactly two places instead of being spread across callers. `@Min(1)` on the parameters rejects
invalid input up front.

---

## What I would improve

### Use `PUT` instead of `POST` for ingestion

The client already knows the `referenceId` before it sends anything, so the resource is fully
identified up front and ingestion is really an upsert on a known URI rather than a create at a
collection endpoint:

```
PUT /payments/{referenceId}
```

`POST` implies "create a new subordinate resource", which fits an endpoint where the server assigns
the identifier — not this one. `PUT` also carries the right guarantee in the protocol itself: it is
idempotent by definition, which is exactly how the endpoint already behaves. Clients and proxies could
rely on that contract instead of having to know the implementation retries safely. The response would
then be `201 Created` on first ingestion and `200 OK` on a subsequent event for the same reference.

### Write more tests

The current coverage is thin: one service unit test and one integration test that only checks a
save-and-read round trip. Worth adding:

- the full ingestion decision matrix — new payment, true duplicate, stale timestamp, lower-ranked
  status, and each valid forward transition
- controller tests with `MockMvc` covering status codes, payload shape, and the `404` path
- pagination edge cases: empty result, last partial page, page beyond the end, invalid `page`/`size`
- mapper tests, so a broken MapStruct mapping is caught by a test and not only by the compiler
