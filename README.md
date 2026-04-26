# WillowWallet 🌿

A personal budget tracking Android app built with Kotlin. WillowWallet lets you log daily expenses, organise them by category, attach photos to entries, and track your spending over custom date ranges — all stored locally on your device.

---


## Features 📜

- **Login & Registration** — secure accounts with SHA-256 password hashing
- **Categories** — create custom spending categories with icons and colours
- **Add Expense** — log an expense with amount, description, date, start/end time, category, and an optional photo
- **Photo Attachments** — take a photo with the camera or pick from the gallery; tap any expense to view the photo fullscreen
- **Expense List** — view all expenses filtered by a custom date range with a running total
- **Budget Goals** — set minimum and maximum monthly spending goals *(in progress)*
- **Reports** — view total spending broken down by category *(in progress)*
- **Offline Storage** — all data is saved locally using Room (SQLite)

---

## Tech Stack 💻

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Architecture | MVVM |
| Database | Room (SQLite) |
| Image Loading | Glide |
| UI | Material Design 3, ViewBinding |
| Async | Kotlin Coroutines + Flow |
| Build | Gradle with KSP |
| CI | GitHub Actions |

---

## Project Structure 🔧

```
com.example.willowwallet
├── data
│   ├── db                  # Room DAOs and database class
│   ├── entities            # User, Category, Expense, BudgetGoal
│   └── repository          # WillowRepository (single source of truth)
├── ui
│   ├── auth                # Login, Register, Splash screens
│   ├── category            # Categories list and add category
│   └── expense             # Add expense, expense list, fullscreen photo
├── utils
│   ├── DateUtils           # Date formatting, time validation, currency
│   ├── HashUtils           # SHA-256 password hashing
│   └── SessionManager      # Stores logged-in user session
├── viewmodel               # AuthViewModel, CategoryViewModel, ExpenseViewModel
└── MainActivity            # Host activity with fragment navigation
```

---

## Getting Started 📍

### Requirements
- Android Studio Hedgehog or later
- Min SDK 24 (Android 7.0)
- Target SDK 36

### Run the app
1. Clone the repository
2. Open in Android Studio
3. Wait for Gradle sync to complete
4. Run on an emulator or physical device

### Run the tests
```bash
./gradlew test
```

---

## Database Schema

| Table | Key Fields |
|-------|-----------|
| `users` | id, username, passwordHash, displayName |
| `categories` | id, userId, name, colorHex, icon, budgetLimit |
| `expenses` | id, userId, amount, date, startTime, endTime, description, categoryId, photoPath |
| `budget_goals` | id, userId, year, month, minGoal, maxGoal |

---

## Currency 💵

All amounts are displayed in **South African Rand (R)** — e.g. `R 125.50`.

---

## Permissions 🔑

| Permission | Reason |
|-----------|--------|
| `CAMERA` | Taking photos for expense entries |
| `READ_MEDIA_IMAGES` | Picking photos from gallery (Android 13+) |
| `READ_EXTERNAL_STORAGE` | Picking photos from gallery (Android 12 and below) |

---

## CI/CD

GitHub Actions runs on every push and pull request to `main`:
- Builds the project
- Runs all unit tests

---

## Group Members 🦋
- Viandra Kistasamy
- Kaitlyn Pillay
- Nikkita Ramsumair

---

## License ⚖️

MIT License
