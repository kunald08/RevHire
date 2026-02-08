-- create a DB 
CREATE DATABASE revhire;
USE revhire;

-- users table;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('JOB_SEEKER','EMPLOYER') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- verify
DESC users;

-- test table 
INSERT INTO users (name, email, password, role)
VALUES ('Test User', 'test@mail.com', '1234', 'JOB_SEEKER');

SELECT * FROM users;

-- job table
CREATE TABLE jobs (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    company VARCHAR(100),
    location VARCHAR(100),
    experience INT,
    salary DOUBLE,
    job_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
select * from jobs;

-- add employer_id
ALTER TABLE jobs
ADD employer_id INT;

-- add fk
ALTER TABLE jobs
ADD CONSTRAINT fk_jobs_employer
FOREIGN KEY (employer_id)
REFERENCES users(id);

DESC jobs;

-- fake data
INSERT INTO jobs
(title, company, location, experience, salary, job_type, employer_id)
VALUES
('Java Developer', 'RevHire', 'Bhopal, MP', 1, 400000, 'FULL_TIME', 1);

-- resume table
CREATE TABLE resumes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    objective TEXT,
    skills TEXT,
    experience TEXT,
    education TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- application table
CREATE TABLE applications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    job_id INT,
    status VARCHAR(50) DEFAULT 'APPLIED',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (job_id) REFERENCES jobs(id)
);

-- for prevention of duplicate
ALTER TABLE applications
ADD CONSTRAINT uq_user_job UNIQUE (user_id, job_id);


-- company table
CREATE TABLE companies (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employer_id INT UNIQUE,
    name VARCHAR(100),
    industry VARCHAR(100),
    location VARCHAR(100),
    description TEXT,
    FOREIGN KEY (employer_id) REFERENCES users(id)
);

-- update jobs table(emp relation)
ALTER TABLE jobs
ADD employer_id INT,
ADD FOREIGN KEY (employer_id) REFERENCES users(id);

-- application status(shortlist/reject)
ALTER TABLE applications
MODIFY status VARCHAR(50) DEFAULT 'APPLIED';

