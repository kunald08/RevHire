# RevHire — Console-Based Job Portal

A **role-based recruitment system** built with Java SE, JDBC, and MySQL.  
Job seekers can search for jobs, build a resume, manage their profile, and track applications.  
Employers can manage company profiles, post jobs, manage applicants, and view hiring statistics — all from the terminal.

---

## Features

### Job Seeker
| Feature | Description |
|---------|-------------|
| Registration | Separate seeker flow with optional profile fields (phone, education, skills, certifications) |
| Login & Password Reset | Email-based auth with security Q&A for password reset |
| Profile Management | Update phone, education, work experience, skills, certifications anytime |
| Profile Completion | Percentage tracker (5 profile fields, 20% each) |
| Resume | Create and update a structured resume (objective, education, experience, skills, **projects**) |
| Job Search | Browse all jobs or filter by title, location, type, experience, **company name, salary range** |
| View Job Details | Full details: description, skills required, education req, salary range, deadline |
| Apply | Apply with optional **cover letter**; validates resume exists and no duplicates |
| Applications | View status, job title, company, date, employer comments; APPLIED/SHORTLISTED/REJECTED/WITHDRAWN |
| Withdraw | Confirmation prompt with optional **reason**; sets status to WITHDRAWN (preserves record) |
| Notifications | In-app alerts with **unread count** badge; mark as read individually or all at once |

### Employer
| Feature | Description |
|---------|-------------|
| Registration | Separate employer flow; prompted to create company after login |
| Company Profile | Create, **view, and update** company profile (name, industry, **size, website**, description, location) |
| Job Management | Post (with **description, skills required, education req, salary range, deadline**), edit, close, reopen, **delete** jobs |
| Applicant View | View all applicants per job with formatted tables |
| Filter Applicants | Filter by skills, education, experience, **application date** |
| Actions | Shortlist / reject individually or **in bulk with notifications** to each applicant |
| Comments | Add review comments on application (triggers notification to applicant) |
| Statistics | View applied / shortlisted / rejected / **withdrawn** counts per job |
| Resume View | View any applicant's resume by application ID |
| Notifications | View notifications with unread badge, mark all as read |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17+ |
| Database | MySQL 8+ |
| Persistence | JDBC (PreparedStatement) |
| Build | Maven |
| Logging | Log4j 2 (console + rolling file) |
| Testing | JUnit 5 |

---

## Project Structure

```
src/main/java/com/revhire/
├── app/            Main.java          — Console UI, menus, input helpers
├── config/         DBConfig.java      — DB connection constants
├── util/           DBUtil.java        — JDBC connection factory
├── model/          User, Job, Company, Application, Resume, Notification
├── dao/            Interfaces (UserDao, JobDao, etc.)
│   └── impl/       JDBC implementations
└── service/        AuthService, JobSeekerService, EmployerService

src/test/java/com/revhire/service/
├── AuthServiceTest.java
├── EmployerServiceTest.java
└── JobSeekerServiceTest.java

sql/
└── schema.sql      — Complete DB schema (6 tables)
```

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                   │
│  ┌───────────────────────────────────────-────────────┐ │
│  │                   Main.java                        │ │
│  │  • Welcome Menu (Register Seeker / Employer / Login) │
│  │  • Job Seeker Menu (15 options)                    │ │
│  │  • Employer Menu (21 options)                      │ │
│  │  • Input validation & display formatting           │ │
│  └──────────────────────┬────────────────────────────┘  │
├─────────────────────────┼───────────────────────────────┤
│                    SERVICE LAYER                        │
│  ┌──────────┐   ┌──────────────┐  ┌──────────────────┐  │
│  │AuthService│  │JobSeekerSvc  │  │  EmployerService │  │
│  │• register │  │• searchJobs  │  │• postJob         │  │
│  │• login    │  │• applyJob    │  │• bulkShortlist   │  │
│  │• password │  │• createResume│  │• filterApplicants│  │
│  │• profile  │  │• withdraw    │  │• deleteJob       │  │
│  │• getUserBy│  │• notifications│ │• updateCompany   │  │
│  └─────┬─────┘  └──────┬───────┘  └────────┬─────────┘  │
├────────┼───────────────┼────────────────────┼───────────┤
│                   DAO LAYER (Interfaces)                │
│  ┌────────┐ ┌──────┐ ┌─────────┐ ┌──────────┐           │
│  │UserDao │ │JobDao│ │ResumeDao│ │CompanyDao│           │
│  └───┬────┘ └──┬───┘ └───┬─────┘ └────┬─────┘           │
│  ┌────────────┐ ┌──────────────┐ ┌──────────────┐       │
│  │ApplicationDao│ │NotificationDao│             │       │
│  └──────┬──────┘ └──────┬───────┘               │       │
├─────────┼──────────────┼────────────────────────┤       │
│                DAO IMPL LAYER                    │      │
│  • PreparedStatement for all queries             │      │
│  • Transaction management (delete job)           │      │
│  • Dynamic WHERE clause building (search/filter) │      │
├──────────────────────────────────────────────────┤      │
│               DATABASE (MySQL 8+)                │      │
│  ┌──────┐ ┌────┐ ┌───────┐ ┌─────────────┐       │      │
│  │users │ │jobs│ │resumes│ │applications │       │      │
│  └──────┘ └────┘ └───────┘ └─────────────┘       │      │
│  ┌─────────┐ ┌──────────────┐                    │      │
│  │companies│ │notifications │                    │      │
│  └─────────┘ └──────────────┘                    │      │
└──────────────────────────────────────────────────┘      │
```

**Design principles:**
- Interface-driven DAO layer (easy to swap implementations)
- Service layer enforces business rules (resume-before-apply, no duplicate applications, ownership checks)
- PreparedStatements prevent SQL injection
- Log4j 2 logs every action to console + `logs/revhire.log`
- Notifications sent automatically on: apply, shortlist, reject, bulk actions, comments
- Profile completion tracking calculated dynamically

---

## ERD (Entity-Relationship Diagram)

```
┌──────────────────────────────┐       ┌─────────────────────────────────┐
│           USERS              │       │           COMPANIES             │
├──────────────────────────────┤       ├─────────────────────────────────┤
│ PK  id            INT AUTO   │       │ PK  id            INT AUTO     │
│     name          VARCHAR    │       │ FK  employer_id   INT (UNIQUE) │──┐
│     email         VARCHAR UQ │       │     name          VARCHAR      │  │
│     password      VARCHAR    │       │     industry      VARCHAR      │  │
│     role          ENUM       │       │     size          VARCHAR      │  │
│     phone         VARCHAR    │       │     description   TEXT         │  │
│     education     TEXT       │       │     website       VARCHAR      │  │
│     work_experience TEXT     │       │     location      VARCHAR      │  │
│     skills        TEXT       │       │     created_at    TIMESTAMP    │  │
│     certifications TEXT      │       └─────────────────────────────────┘ │
│     security_question VARCHAR│                                           │
│     security_answer VARCHAR  │                                           │
│     created_at    TIMESTAMP  │                                           │
└──────────┬───────────────────┘                                           │
           │                                                               │
           │  1:1                                                   1:1    │
           │                                                               │
           ├───────────────────────────────────────────────────────────────┘
           │
           │  1:N                   1:N
           │                         │
    ┌──────┴──────────────────┐     ┌┴────────────────────────────────────┐
    │        RESUMES          │     │              JOBS                   │
    ├─────────────────────────┤     ├─────────────────────────────────────┤
    │ PK  id        INT AUTO  │     │ PK  id              INT AUTO        │
    │ FK  user_id   INT (UQ)  │     │ FK  employer_id     INT             │
    │     objective TEXT      │     │     title           VARCHAR         │
    │     education TEXT      │     │     description     TEXT            │
    │     experience TEXT     │     │     company         VARCHAR         │
    │     skills    TEXT      │     │     location        VARCHAR         │
    │     projects  TEXT      │     │     experience      INT             │
    │     created_at TIMESTAMP│     │     salary_min      DECIMAL         │
    └─────────────────────────┘     │     salary_max      DECIMAL         │
                                    │     job_type        ENUM            │
                                    │     skills_required TEXT            │
           │                        │     education_req   VARCHAR         │
           │                        │     deadline        DATE            │
           │                        │     status          ENUM            │
           │                        │     created_at      TIMESTAMP       │
           │                        └──────────┬──────────────────────────┘
           │                                   │
           │  1:N                              │  1:N
           │                                   │
           │    ┌──────────────────────────────┴──────────────────────┐
           │    │                APPLICATIONS                        │
           │    ├────────────────────────────────────────────────────┤
           │    │ PK  id              INT AUTO                       │
           │    │ FK  user_id         INT    ──── USERS.id           │
           │    │ FK  job_id          INT    ──── JOBS.id            │
           │    │     status          ENUM (APPLIED/SHORTLISTED/     │
           │    │                          REJECTED/WITHDRAWN)       │
           │    │     cover_letter    TEXT                           │
           │    │     withdraw_reason VARCHAR                        │
           │    │     comment         TEXT                           │
           │    │     applied_at      TIMESTAMP                      │
           │    │     UNIQUE(user_id, job_id)                        │
           │    └────────────────────────────────────────────────────┘
           │
           │  1:N
           │
    ┌──────┴──────────────────────────────────────────┐
    │              NOTIFICATIONS                       │
    ├──────────────────────────────────────────────────┤
    │ PK  id              INT AUTO                     │
    │ FK  user_id         INT    ──── USERS.id         │
    │     message         TEXT                         │
    │     is_read         BOOLEAN (DEFAULT FALSE)      │
    │     created_at      TIMESTAMP                    │
    └──────────────────────────────────────────────────┘

Relationships:
  USERS  1 ──── 1  RESUMES      (one resume per job seeker)
  USERS  1 ──── 1  COMPANIES    (one company per employer)
  USERS  1 ──── N  JOBS         (employer posts many jobs)
  USERS  1 ──── N  APPLICATIONS (seeker applies to many jobs)
  JOBS   1 ──── N  APPLICATIONS (one job has many applicants)
  USERS  1 ──── N  NOTIFICATIONS (one user gets many notifications)
```

---

## Database Schema

Six tables with foreign keys and constraints:

| Table | Purpose | Key Columns |
|-------|---------|-------------|
| `users` | Accounts with role (JOB_SEEKER / EMPLOYER) and profile | phone, education, work_experience, skills, certifications |
| `jobs` | Listings with full details | description, skills_required, education_req, salary_min/max, deadline |
| `resumes` | One resume per user (UNIQUE constraint) | objective, education, experience, skills, **projects** |
| `applications` | User→Job with status tracking | cover_letter, withdraw_reason, status (4 states), comment |
| `companies` | One company profile per employer | size, website, industry, description |
| `notifications` | Alerts for status changes, comments, etc. | is_read, created_at |

---

## Setup & Run

### Prerequisites
- Java 17 or higher
- MySQL 8+
- Maven 3.8+

### 1. Create the database

```bash
mysql -u root -p < sql/schema.sql
```

### 2. Configure connection

Edit `src/main/java/com/revhire/config/DBConfig.java`:

```java
public static final String URL  = "jdbc:mysql://localhost:3306/revhire";
public static final String USER = "root";
public static final String PASS = "your_password";
```

### 3. Build

```bash
mvn clean compile
```

### 4. Run (from IntelliJ IDEA)

Open the project in IntelliJ → run `Main.java` (right-click → Run).

Or from terminal:

```bash
mvn -q exec:java -Dexec.mainClass="com.revhire.app.Main"
```

### 5. Run tests

```bash
mvn test
```

> **Note:** Tests are integration tests that require a running MySQL instance with the `revhire` database.

---

## Business Rules

- **Separate registration flows** — Job Seekers provide profile fields; Employers are prompted to create a company
- **Profile completion tracking** — 5 profile fields (phone, education, work experience, skills, certifications); percentage shown in menu
- **Resume required** — job seekers must create a resume before applying
- **No duplicate applications** — enforced at both service and DB level (UNIQUE constraint)
- **Cover letter support** — optional cover letter submitted with each application
- **Withdraw with reason** — confirmation prompt; sets status to WITHDRAWN (preserves audit trail)
- **Role-based access** — job seekers and employers see completely different menus
- **Employer ownership** — employers can only edit/close/delete their own jobs
- **Delete job** — cascading delete removes all associated applications (with confirmation)
- **Notifications** — automatic alerts on: apply, shortlist, reject, bulk actions, comments
- **Unread badge** — notification count shown in menu header
- **Salary range** — jobs specify min/max salary; search supports range filtering
- **Password security** — change password requires old password; reset requires security Q&A

---

## Application Statuses

| Status | Description |
|--------|-------------|
| `APPLIED` | Initial state when seeker applies |
| `SHORTLISTED` | Employer moved applicant forward |
| `REJECTED` | Employer declined the application |
| `WITHDRAWN` | Seeker withdrew their application (with optional reason) |

---

## Logging

All actions are logged via Log4j 2:
- **Console** — compact format (`HH:mm:ss LEVEL Class — message`)
- **File** — detailed format in `logs/revhire.log` (rolling)
- Events logged: registration, login, job posting, applications, status changes, bulk actions, errors

---

## License

This project is for educational purposes.
