# MES Complaint System

A professional Java Swing desktop application designed to manage complaints efficiently for personnel and administrators. This project demonstrates my skills in Java GUI development, database integration, and object-oriented design.

---

## Key Highlights

- Developed a multi-role system supporting Admin, Operator, and Technician workflows.
- Built complaint logging, tracking, and reporting with a structured database backend.
- Implemented DAO pattern to manage database operations cleanly and efficiently.
- Designed user-friendly dashboards for each role using Java Swing.
- Provided self-service complaint forms to streamline user interaction.
- Created dynamic reports for complaints and personnel activities.
- Structured the project to be modular and maintainable.

---

## Technologies Used

- Java (JDK 8+)
- Swing (GUI)
- MySQL (database)
- DAO Pattern
- Manual Build / Compilation

---

## Project Features

- Role-based access for Admin, Operator, Technician
- Track complaint status and priority dynamically
- Generate reports on personnel performance and complaint resolution
- Modular, scalable codebase
- GUI designed for intuitive navigation

---

## Project Structure

MES-Complaint-System/
├── src/                  # All source code (DAO, Model, View)
├── lib/                  # MySQL connector JAR
├── sql/                  # Database creation scripts
├── docs/screenshots/     # Screenshots for demonstration
├── .vscode/              # IDE settings
├── .gitignore            # Files/folders to ignore
└── README.md             # This file

---

## How to Run

1. Setup MySQL database:
   - Run `sql/sql/create_tables.sql`
   - Update database credentials in `DBConnection.java`
2. Compile the project:

```bash
javac -cp "lib/mysql-connector-j-9.5.0.jar" -d bin src/com/mes/dao/*.java src/com/mes/model/*.java src/com/mes/view/*.java
Run the application:
java -cp "bin:lib/mysql-connector-j-9.5.0.jar" com.mes.view.AppMain
Screenshots

Add GUI screenshots in docs/screenshots/ to showcase:

Login Screen
Admin Dashboard
Operator Dashboard
Technician Dashboard
Log Complaint Form
Reports
What You Will Learn
Best practices in OOP and modular design
Integrating Java Swing GUI with MySQL
Role-based access control
Managing data flow and database operations via DAO pattern
Creating professional, maintainable code
License

Include a license of your choice (MIT or Apache 2.0 recommended).

Contact / Support

Open an issue in the repository for support or questions
