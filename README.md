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
- Instant result display after submission

---

### Admin Side
- Add multiple questions at once using formatted input
- View all questions
- Delete questions individually
- View all quiz results using endpoint
- Clear all results when needed

---

## Admin Question Management

Questions can be added in bulk using the following format:
Q: Question text
A: Option 1
B: Option 2
C: Option 3
D: Option 4
ANS: Correct Answer

Separate multiple questions using:

---

## Result Management

Results are stored in the database after each attempt.

Each result contains:
- Username
- Score
- Total Questions
- Pass / Fail Status
- Attempt Time

### Result Access
- `/admin/results` → View all attempt results
- `/admin/clear-results` → Delete all stored results

(Admin endpoints are intended for controlled/internal use)

---

## How Scoring Works
- Each correct answer = 1 mark
- Final score is calculated based on correct answers
- Passing criteria:
- Pass if score >= 60% of total questions
Fail otherwise

---

## Timer Logic
- Total time = number of questions × 30 seconds
- Auto-submit happens when timer reaches zero

---

## Technologies Used
- Java
- Spring Boot
- REST API
- HTML, CSS, JavaScript
- MySQL / TiDB Cloud

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

## Summary
This project demonstrates:
- Full-stack development with Spring Boot
- REST API integration
- Dynamic quiz system with timer
- Bulk question upload feature
- Result tracking with admin control
- Cloud deployment using Render and TiDB Cloud

---

## Note
This project does not include authentication system.  
Users are identified using name input only.

---

## Author
Built as a learning project to demonstrate full-stack web development skills.
