# Smart Health & Fitness Tracker

A Java-based Android application for tracking daily steps, water intake, and estimated calories burned. Built as a group project.

## Current Features
- Login screen (placeholder auth)
- Dashboard: steps, water intake, calorie estimate
- Local persistence via SharedPreferences
- Activity log screen

## Getting Started
1. Open the project folder in **Android Studio** (`File > Open`, select this folder)
2. Let Gradle sync
3. Run on an emulator or physical device (min SDK 21)

## Project Structure
```
app/src/main/java/com/example/healthfit/
  LoginActivity.java      - login screen logic
  DashboardActivity.java  - main tracking screen
  LogActivity.java        - activity history screen
app/src/main/res/layout/  - XML screen layouts
```

## Suggested Next Steps (divide among team)
- [ ] Real step counting via `Sensor.TYPE_STEP_COUNTER`
- [ ] Move from SharedPreferences to SQLite/Room for multi-day history
- [ ] User profile (age, weight, height) for accurate calorie math
- [ ] Charts for weekly trends (e.g. MPAndroidChart library)
- [ ] Real authentication (Firebase Auth or a backend API)
- [ ] Notifications/reminders (e.g. "drink water", "time to walk")

## Git Workflow
- `main` = stable/demo-ready code only
- `dev` = integration branch
- `feature/<name>` = individual work branches

Each contributor:
```bash
git checkout -b feature/your-feature-name
git add .
git commit -m "Description of change"
git push origin feature/your-feature-name
```
Then open a Pull Request into `dev` for review before merging.
