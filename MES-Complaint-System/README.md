# MES Complaint System

A Java Swing-based desktop application to manage complaints for personnel and administrators. Fully integrated with MySQL.

---

## Features

- Multi-role system: Admin, Operator, Technician
- Complaint logging, tracking, and reporting
- Personnel and user management
- Self-service complaint forms
- Dashboard views for each role
- Clean and professional Java Swing GUI
- Report generation for complaints and personnel
- Fully functional DAO pattern for database operations

---

## Prerequisites

- Java JDK 8 or higher
- MySQL server installed
- IDE (optional: VS Code, IntelliJ IDEA, Eclipse)
- MySQL Connector JAR (already included in `lib/` folder)

---

## Database Setup

1. Start MySQL server.
2. Run the SQL script located at:

```
sql/sql/create_tables.sql
```

3. Update `DBConnection.java` with your MySQL credentials:

```java
private static final String URL = "jdbc:mysql://localhost:3306/university";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

---

## Compile the Project

Open terminal/command prompt at the project root and run:

```bash
javac -cp "lib/mysql-connector-j-9.5.0.jar" -d bin src/com/mes/dao/*.java src/com/mes/model/*.java src/com/mes/view/*.java
```

- This compiles all source files into the `bin/` directory.

---

## Run the Application

```bash
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" com.mes.view.AppMain
```

- The login screen will appear first.
- Use Admin, Operator, or Technician credentials to explore dashboards and functionalities.

---

## Screenshots

Place GUI screenshots under `docs/screenshots/`. Recommended screenshots:

- Login Screen
- Admin Dashboard
- Operator Dashboard
- Technician Dashboard
- Log Complaint Form
- Reports

---

## Project Structure

MES-Complaint-System/
├── src/                  # All .java source files (DAO, Model, View)
├── lib/                  # MySQL connector JAR
├── sql/                  # Database scripts
├── docs/screenshots/     # Screenshots of GUI
├── .vscode/              # IDE settings
├── .gitignore            # Files/folders to ignore in Git
└── README.md             # This file

---

## GitHub Repository Setup

**Repository Name:** MES-Complaint-System

Steps to upload manually:

1. Log in to GitHub.
2. Click **New Repository** → Name: `MES-Complaint-System`.
3. Choose **Public** or **Private**.
4. **Do not initialize with README** (we already have one locally).
5. Click **Create repository**.
6. Open repository → **Add file → Upload files**.
7. Drag and drop all project folders and files:

```
src/
lib/
sql/
docs/
.vscode/
.gitignore
README.md
```

8. Add commit message: `Initial commit: full project upload`.
9. Click **Commit changes**.
10. Verify folder structure and README on GitHub.

---

## Notes

- Ensure MySQL service is running before launching the application.
- Java 8+ is required.
- Follow folder structure strictly for proper compilation and execution.
- Screenshots enhance repository readability.
- Optional: Add GitHub Actions for CI/CD to compile and test on push.

---

## License

Include a license of your choice (MIT or Apache 2.0 recommended for open-source clarity).

---

## Contact / Support

For any issues or support, open an issue in the GitHub repository.
