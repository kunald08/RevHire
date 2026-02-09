-- ============================================================
--  RevHire — Complete Database Schema
--  Run:  mysql -u root -p < sql/schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS revhire;
USE revhire;

-- Drop in FK-safe order
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS applications;
DROP TABLE IF EXISTS resumes;
DROP TABLE IF EXISTS jobs;
DROP TABLE IF EXISTS companies;
DROP TABLE IF EXISTS users;

-- ── Users ──────────────────────────────────────────────────
CREATE TABLE users (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    name              VARCHAR(100)  NOT NULL,
    email             VARCHAR(100)  UNIQUE NOT NULL,
    password          VARCHAR(255)  NOT NULL,
    role              ENUM('JOB_SEEKER','EMPLOYER') NOT NULL,
    phone             VARCHAR(20),
    education         VARCHAR(255),
    work_experience   VARCHAR(500),
    skills            VARCHAR(500),
    certifications    VARCHAR(500),
    security_question VARCHAR(255),
    security_answer   VARCHAR(255),
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── Companies ──────────────────────────────────────────────
CREATE TABLE companies (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    employer_id   INT UNIQUE,
    name          VARCHAR(100) NOT NULL,
    industry      VARCHAR(100),
    size          VARCHAR(50),
    description   TEXT,
    website       VARCHAR(255),
    location      VARCHAR(100),
    FOREIGN KEY (employer_id) REFERENCES users(id)
);

-- ── Jobs ───────────────────────────────────────────────────
CREATE TABLE jobs (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    employer_id      INT NOT NULL,
    title            VARCHAR(100) NOT NULL,
    description      TEXT,
    company          VARCHAR(100),
    location         VARCHAR(100),
    skills_required  VARCHAR(500),
    experience       INT            DEFAULT 0,
    education_req    VARCHAR(255),
    salary_min       DOUBLE         DEFAULT 0,
    salary_max       DOUBLE         DEFAULT 0,
    job_type         VARCHAR(50),
    status           VARCHAR(20)    DEFAULT 'OPEN',
    deadline         DATE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employer_id) REFERENCES users(id)
);

-- ── Resumes ────────────────────────────────────────────────
CREATE TABLE resumes (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT UNIQUE,
    objective   TEXT,
    education   TEXT,
    experience  TEXT,
    skills      TEXT,
    projects    TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ── Applications ───────────────────────────────────────────
CREATE TABLE applications (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NOT NULL,
    job_id          INT NOT NULL,
    cover_letter    TEXT,
    status          VARCHAR(50) DEFAULT 'APPLIED',
    comment         TEXT,
    withdraw_reason TEXT,
    applied_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (job_id)  REFERENCES jobs(id),
    CONSTRAINT uq_user_job UNIQUE (user_id, job_id)
);

-- ── Notifications ──────────────────────────────────────────
CREATE TABLE notifications (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT NOT NULL,
    message    TEXT,
    is_read    BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

