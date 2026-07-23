## Local Setup: Postgres Database

The backend expects a Postgres instance when running with the `dev` profile.

1. **Copy the environment file** and edit if necessary:
```bash
cp .env.example .env
```
2. **Export the required variables** in your terminal (values below match `.env.example` —
    adjust if you changed them):
```powershell
$env:POSTGRES_DB="student_skeleton"
$env:POSTGRES_USER="student_skeleton"
$env:POSTGRES_PASSWORD="change-me"
$env:POSTGRES_PORT="5432"
```
3. **Start the Postgres container:**
```bash
docker-compose up -d
```
Confirm it's healthy before continuing:
```bash
docker ps
```
`student-skeleton-db` should show `(healthy)` in the `STATUS` column after a few seconds.

4. **Run the backend against it:**
```bash
./backend/gradlew -p ./backend/ bootRun --args='--spring.profiles.active=dev'
```
5. **Stop the database and wipe the data volume** when you're done:
```bash
docker-compose down -v
```

**NOTES:**
- Exported variables only last for the current terminal session — re-run step 2 if you open a new one.
- Tests do **not** require the container to be running — `application-test.yaml` uses an in-memory H2
    database in Postgres-compatibility mode.
- If `docker-compose up -d` fails or `docker ps` never shows `(healthy)`, make sure Docker Desktop is
    actually running first.
