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

1. **Generating the OpenAPI Spec Only:** Boots the Spring Boot application in a forked process,
extracts the raw OpenAPI definition, and writes it to `backend/build/openapi.json`.
```bash
./gradlew generateOpenApiDocs
```
2. **Generate Full TypeScript Client (Recommended):** Runs generateOpenApiDocs first,
then uses the specification to generate a typed TypeScript client inside frontend/src/api/generated/.
```bash
./gradlew generateApiClient
```
**NOTES:**
- It is not necessary to manually modify files inside frontend/src/api/generated/ —
they are generated dynamically and excluded from version control.

- If you are encountering problems in running the commands in this section,
make sure the Gradle JVM version is set to 24.

---
## Javadoc Check

Public classes and methods in the controller, service, repository, and mapper packages
must include Javadoc. This rule is enforced by Checkstyle and is validated in the CI
pipeline for every pull request.

Before pushing your changes, run the following command to verify that all required
Javadoc is present:

```bash
./gradlew checkstyleMain
```

**NOTES:**
- The check fails if any required Javadoc is missing from a public class or method.
- Checkstyle reports the file and line number for each violation, making it easier to
identify and fix missing documentation.
- Ensure the check passes locally before opening or updating a pull request.
---
## Additional Resources
[text]
