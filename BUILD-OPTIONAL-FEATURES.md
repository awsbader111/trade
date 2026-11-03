# Completing the optional features (+10 points)

The new Test page (`/test`) is part of Spring Boot and needs no JavaScript build.
The React app is authored as real React components, uses Axios, and must be **built with Vite** (not Babel standalone).

## First-time React build (internet access required on your computer)

Open terminal in the `trade` project root:

```powershell
cd frontend
npm install
npm run build
cd ..
```

This produces the files in `src/main/resources/static/react/` (the React `index.html` and hashed assets).
**Then** rebuild the Java application using JDK 25:

```powershell
.\mvnw.cmd clean package
Copy-Item target\homework.jar .\homework.jar -Force
```

Run from root:

```powershell
java -jar homework.jar
```

Open:
- http://localhost:8080/test — four-operation calculator
- http://localhost:8080/react/index.html — React/Axios students CRUD

H2 saves to `C:\data\atlasfx.mv.db` on Windows. The application creates `C:\data` when starting from `main()`.
Do not delete this file if you want to keep student records. On Linux/macOS set `ATLASFX_DB_URL` to a writable absolute H2 file path.

## Required verification

1. Run `mvnw.cmd test` and capture a screenshot showing passing CalculatorTest, CalculatorControllerTest, and StudentRestControllerTest.
2. Create a student in React; refresh; update them; delete them; refresh. Screenshot each meaningful stage.
3. Close and restart the Java app: confirm any remaining students persist.
4. Run `java -jar homework.jar` outside IntelliJ and check both new menu links.
5. Only after successful build, commit both source files, React build output, and the newly copied root JAR to GitHub.

## IMPORTANT: professor's exact choice-of-database requirement

The professor says to **select one database from a linked Google Drive folder**. That folder was not accessible in the supplied materials, so a `students` table based on the professor's *Java Lecture JDBC* example has been implemented as a provisional substitute. To claim the full React optional +5 points, compare it with an actual database from the provided Drive folder or replace it with a chosen supplied table.

Do not upload `password.txt`, your real API token, `.env` secrets, or `node_modules` to public GitHub.
