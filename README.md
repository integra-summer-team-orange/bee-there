# BeeThere

[App description]

---
## Prerequisites
[text]

---
## Regenerating the Frontend API Client

---
When backend controller endpoints, request/response DTOs, or OpenAPI annotations are
updated, you must regenerate the TypeScript API client to keep the frontend types in sync.
This can be achieved through the two commands below:

1. **Generating the OpenAPI Spec Only:** Boots the Spring Boot application in a forked process, extracts the raw OpenAPI definition,
   and writes it to `backend/build/openapi.json`.
   ```bash
   ./gradlew generateOpenApiDocs
2. **Generate Full TypeScript Client (Recommended):** Runs generateOpenApiDocs first,
   then uses the specification to generate a typed TypeScript client inside frontend/src/api/generated/.
   ```bash
   ./gradlew generateApiClient
**NOTES:**
- It is not necessary to manually modify files inside frontend/src/api/generated/ —
  they are generated dynamically and excluded from version control.

- If you are encountering problems in running the commands in this section,
  make sure the Gradle JVM version is set to 24.

---
## Additional Resources
[text]
