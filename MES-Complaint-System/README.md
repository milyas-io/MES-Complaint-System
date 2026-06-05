# MES Complaint Management System

A desktop-based Complaint Management System developed in Java to streamline the process of registering, tracking, and managing complaints. The system provides an organized way for users to submit complaints and for administrators to monitor and resolve them efficiently.

## Project Overview

The MES Complaint Management System was developed as an academic software engineering project to apply object-oriented programming, database management, and software design principles in a real-world scenario. The application follows a layered architecture using Java, JDBC, and MySQL.

## Features

* User registration and authentication
* Complaint submission and management
* Complaint status tracking
* Database-driven data storage
* Administrator dashboard for complaint handling
* Structured GUI-based interface
* CRUD operations for complaint records
* Secure database connectivity using JDBC

## Technologies Used

* Java
* Java Swing (GUI)
* JDBC
* MySQL
* Object-Oriented Programming (OOP)
* DAO (Data Access Object) Design Pattern
* Git & GitHub

## Project Structure

```text
MES-Complaint-System/
├── src/                 # Java source files
│   ├── dao/             # Data Access Layer
│   ├── model/           # Business Models
│   └── view/            # GUI Components
├── lib/                 # External libraries
├── sql/                 # Database scripts
├── docs/screenshots/    # Application screenshots
├── .vscode/             # IDE settings
├── .gitignore
└── README.md
```

## System Architecture

The project follows a layered architecture:

* View Layer → User Interface (Java Swing)
* Model Layer → Business Objects and Data Models
* DAO Layer → Database Access and Queries
* Database Layer → MySQL Database

This separation improves maintainability, scalability, and code organization.

## Database

The application uses MySQL for persistent storage.

The SQL scripts required to create the database schema are available in:

```text
sql/
```

Import the provided scripts before running the application.

## Getting Started

### Prerequisites

* Java JDK 8 or higher
* MySQL Server
* MySQL Connector/J

### Installation

1. Clone the repository:

```bash
git clone https://github.com/yourusername/MES-Complaint-System.git
```

2. Create the database using the SQL scripts in the `sql/` folder.

3. Configure database credentials in the JDBC connection file.

4. Add the MySQL Connector JAR from the `lib/` directory.

5. Compile and run the application.

## Screenshots

Application screenshots can be found in:

```text
docs/screenshots/
```

## Learning Outcomes

Through this project, I gained practical experience in:

* Object-Oriented Software Development
* Java Desktop Application Development
* Database Design and Integration
* JDBC Connectivity
* DAO Design Pattern
* Software Architecture and Project Organization
* Version Control using Git and GitHub

## Future Improvements

* Role-based access control
* Email notifications
* Complaint analytics dashboard
* Web-based version of the system
* REST API integration
* Enhanced reporting and tracking features

## Author

## Muhammad Ilyas

BS Computer Science Student

Interested in Software Engineering, Machine Learning, and AI.

Feel free to connect or provide feedback regarding the project.
