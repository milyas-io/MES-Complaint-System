
-- MES COMPLAINT MANAGEMENT SYSTEM


-- 1. DATABASE CREATION

DROP DATABASE IF EXISTS mes_complaints;
CREATE DATABASE mes_complaints;
USE mes_complaints;

-- 2. PERSONNEL TABLE

CREATE TABLE personnel (
    service_number VARCHAR(20) PRIMARY KEY,
    personnel_rank VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    unit VARCHAR(100) NOT NULL
);

-- 3. USERS TABLE

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role ENUM('Admin','Operator','Engineer') NOT NULL,
    status ENUM('Available','Busy') DEFAULT 'Available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. COMPLAINTS TABLE

CREATE TABLE complaints (
    complaint_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    priority ENUM('Low','Medium','High') NOT NULL,
    status ENUM('Pending','In Progress','Resolved','Closed') DEFAULT 'Pending',
    remarks TEXT,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolution_date TIMESTAMP NULL,
    logged_by_operator_id INT,
    assigned_to_engineer_id INT,
    complainant_service_number VARCHAR(20),
    FOREIGN KEY (logged_by_operator_id)
        REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to_engineer_id)
        REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (complainant_service_number)
        REFERENCES personnel(service_number) ON DELETE SET NULL
);

-- 5. FEEDBACK TABLE

CREATE TABLE feedback (
    feedback_id INT AUTO_INCREMENT PRIMARY KEY,
    complaint_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    feedback_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (complaint_id)
        REFERENCES complaints(complaint_id) ON DELETE CASCADE
);

-- DEMONSTRATION DATA INSERTION


-- 6. PERSONNEL DATA

INSERT INTO personnel VALUES
('SN101','Major','Ali Raza','Signals Wing'),
('SN102','Captain','Usman Tariq','Electrical Wing'),
('SN103','Lieutenant','Hassan Javed','Civil Works'),
('SN104','Subedar','Imran Khan','Mechanical Wing'),
('SN105','Naib Subedar','Sajid Mehmood','Maintenance Unit');


-- 7. USERS DATA

INSERT INTO users (username,password_hash,name,email,role,status) VALUES
('admin','admin123','System Administrator','admin@mes.gov.pk','Admin','Available'),

('operator1','op123','Operator Alpha','operator1@mes.gov.pk','Operator','Available'),
('operator2','op123','Operator Bravo','operator2@mes.gov.pk','Operator','Available'),

('engineer1','eng123','Engineer Aslam','eng1@mes.gov.pk','Engineer','Available'),
('engineer2','eng123','Engineer Bilal','eng2@mes.gov.pk','Engineer','Busy'),
('engineer3','eng123','Engineer Kamran','eng3@mes.gov.pk','Engineer','Available');


-- 8. COMPLAINTS DATA

INSERT INTO complaints
(title,description,priority,status,remarks,
 creation_date,resolution_date,
 logged_by_operator_id,assigned_to_engineer_id,complainant_service_number)
VALUES

('Power Failure',
 'Complete power outage in barracks',
 'High','Pending',NULL,
 NOW() - INTERVAL 10 DAY,NULL,
 2,NULL,'SN101'),

('Water Leakage',
 'Leakage in underground pipeline',
 'Medium','In Progress','Work started',
 NOW() - INTERVAL 8 DAY,NULL,
 2,4,'SN102'),

('AC Not Working',
 'AC unit malfunction in admin office',
 'Low','Resolved','Cooling restored',
 NOW() - INTERVAL 20 DAY,
 NOW() - INTERVAL 5 DAY,
 3,5,'SN103'),

('Road Damage',
 'Cracks observed on internal road',
 'High','Closed','Repair approved and closed',
 NOW() - INTERVAL 25 DAY,
 NOW() - INTERVAL 15 DAY,
 3,4,'SN104'),

('Generator Noise',
 'Excessive noise from generator room',
 'Medium','In Progress','Inspection ongoing',
 NOW() - INTERVAL 3 DAY,NULL,
 2,6,'SN105');


-- 9. FEEDBACK DATA

INSERT INTO feedback (complaint_id,rating,comments) VALUES
(3,5,'Excellent response and quick resolution'),
(4,4,'Work completed satisfactorily');

