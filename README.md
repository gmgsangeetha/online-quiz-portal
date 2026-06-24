# Online Quiz Portal (Spring Boot Full Stack Project)

## Overview
This is a full-stack Online Quiz Portal built using Spring Boot, HTML, CSS, JavaScript, and MySQL-compatible database.  
It allows users to attempt quizzes, view instant results, and provides an admin panel for question and result management.

---

## Key Features

### Student Side
- Enter name and start quiz instantly (no login required)
- Dynamic question loading from backend
- Timer-based quiz (30 seconds per question)
- Auto-submit when time ends
- Navigation using Previous / Next buttons
- Instant result display after quiz submission
- Displays total questions, correct answers, incorrect answers, and status(pass/fail)
- Tracks total time taken by the student to attempt the quiz
- Students can review answers using:
  - "Show Correct" button
  - "Show Incorrect" button
- Automatic result evaluation and scoring

---

### Admin Side
- Admin needs to login first.
- Add multiple questions at once using formatted input
- View all questions
- Delete questions individually
- View all quiz results 
- Clear all results when needed

---

## Admin Question Management

Admin can add multiple questions in bulk using a structured format, allowing faster question entry without manual one-by-one insertion.

### Bulk Upload Format:
 Q: Question text  
 A: Option 1  
 B: Option 2  
 C: Option 3  
 D: Option 4  
 ANS: Correct Answer  

Multiple questions can be separated using ---

---

## Result Management

The system stores quiz results after each attempt, enabling easy tracking of user performance.

Each result includes:
- Username  
- Score  
- Total Questions  
- Pass / Fail Status  
- Attempt Timestamp  

## Admin Controls (Restricted Access)

- `/admin/results` → View all quiz attempt results  
- `/admin/clear-results` → Delete all stored results  

These endpoints are intended for internal/admin use only. They help in monitoring quiz attempts and managing results **without requiring direct database access**.

⚠️ Note: These URLs are fully secured and can only be accessed if the administrator is actively logged in. If anyone attempts to visit or guess these endpoints directly through the browser address bar without logging in, the server will block them instantly and return an "HTTP 401 Unauthorized" access error.

---

## How Scoring Works
- Each correct answer = 1 mark
- Final score is calculated based on correct answers
- Passing Criteria:  
  Pass: score ≥ 60%  
  Fail: score < 60%

---

## Timer Logic
- Total time = number of questions × 30 seconds
- Auto-submit happens when timer reaches zero

---
## Technologies Used

- Frontend: HTML, CSS, JavaScript  
- Backend: Java, Spring Boot  
- Database: MySQL  
- ORM: Spring Data JPA (Hibernate)  
- IDE: Eclipse (Java EE IDE)   

---

## Architecture

This project follows a simple client–server architecture.

Frontend (HTML/CSS/JavaScript) communicates with Backend (Spring Boot REST APIs), and data is stored in a MySQL-compatible database.

### System Flow

Frontend (HTML, CSS, JavaScript)
        ↓
REST API (Spring Boot Controllers)
        ↓
Service Layer (Business Logic)
        ↓
Repository Layer (Spring Data JPA)
        ↓
Database (MySQL / TiDB Cloud)

### Key Points
- Frontend handles UI, quiz navigation, and timer logic
- Backend handles question fetching, scoring, and result processing
- Database stores questions and quiz results
- Communication happens through REST APIs (JSON format)

---

## Deployment / Hosting

### Backend Hosting
- Hosted using **Render**
- Spring Boot application deployed as a web service
- Auto-builds from GitHub repository

### Database Hosting
- **TiDB Cloud (MySQL compatible database)**
- Stores questions and quiz results

### Frontend
- Integrated with Spring Boot (static HTML/CSS/JS)
- No separate frontend hosting required

### Deployment Flow
GitHub → Render → Spring Boot Backend → TiDB Cloud Database

---

## Note

- This project does not include an authentication system for students since it a simple quiz portal.
- Users are identified using name input only.
- Admin can add, delete, view questions only if he/she has logged in .
> 🔒 **Admin Security Note:** Admin login credentials are maintained secretly to avoid any misuse by others. Hence, if anybody is interested to view the project's administrative features or live dashboards, please contact me via **[LinkedIn](https://linkedin.com/in/gmgsangeetha)** to request temporary access.

---
## Future Scope

- Add secure login and user authentication for students
- Add timer pause/resume protection
- Store full attempt history per user
- Improve UI with leaderboard and analytics
- Add difficulty levels and categories for quizzes

---
## 🔗 Live Project Links

-  Student Quiz Portal: https://online-quiz-portal-5pax.onrender.com/
-  Admin Dashboard: https://online-quiz-portal-5pax.onrender.com/admin-login.html

---
## Purpose of the Project

I have built a basic Online Quiz Portal using Spring Boot to gain exposure to modern backend development.

This project uses core Spring Boot concepts such as RESTful APIs (@RestController, @GetMapping, @PostMapping) and dependency injection (@Autowired).

It also includes Spring Data JPA for database operations with MySQL and a simple service-layer for business logic.

Overall, this project focuses on fundamental Spring Boot concepts and CRUD-based backend development with frontend integration.

---

## GitHub Repository
[Online Quiz Portal](https://github.com/gmgsangeetha/online-quiz-portal)
