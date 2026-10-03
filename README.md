# Task 4 - Online Examination System (JDK 25)

Easy Java Swing project with **no Maven, no Spring Boot, and no external libraries**.
It is designed to open directly in VS Code and run with JDK 25.

## Features
- Login screen
- Profile/name update screen
- 5 MCQ questions
- Radio-button options
- Next/Previous navigation
- 30-minute visible countdown timer
- Automatic submission when time reaches zero
- Manual submit confirmation
- Result score and correct/incorrect breakdown
- Window close confirmation during exam
- Logout back to login

## Demo Login
Username: `student`
Password: `1234`

## Run in VS Code
Open this folder in VS Code.

### PowerShell
```powershell
javac -d out src\OnlineExam.java
java -cp out OnlineExam
```

### Or use the included batch file
Double-click `run.bat`.

## JDK
Designed for JDK 25. Check with:
```powershell
java -version
javac -version
```
