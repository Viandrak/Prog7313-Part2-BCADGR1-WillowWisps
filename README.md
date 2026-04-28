# WillowWallet 🌿

A personal budget tracking Android app built with Kotlin. WillowWallet lets you log daily expenses, organise them by category, attach photos to entries, and track your spending over custom date ranges — all stored locally on your device.

---

## Demo Video 🎬

> 🔗 **[Watch the full app demo on YouTube](https://youtu.be/VX25Gpuecog?si=6C0lLFg7FwIl6rNQ)**
> 
> Full walkthrough with voiceover covering all app features. 

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
- Viandra Kistasamy - ST10445089
- Kaitlyn Pillay - ST10437630
- Nikkita Ramsumair - ST10445383

---

## References 📚

   **Room Database for data persistence:**
   Android Developers, 2025. Room persistence library. [Online] Available at: https://developer.android.com/training/data-storage/room [Accessed 25 April 2026].

   **ViewModel for managing UI data:**
   Android Developers, 2024. ViewModel overview. [Online] Available at: https://developer.android.com/topic/libraries/architecture/viewmodel [Accessed 25 April 2026].

   **Glide for image loading:**
   Bumptech, 2025. Glide image loading library. [Online] Available at: https://bumptech.github.io/glide/ [Accessed 26 April 2026].

   **Camera functionality for receipt photos:**
   Android Developers, 2024. Take photos. [Online] Available at: https://developer.android.com/media/camera/get-started-with-camera [Accessed 26 April 2026].

   **Kotlin programming language:**
   JetBrains, 2025. Kotlin documentation. [Online] Available at: https://blog.jetbrains.com/kotlin/2025/12/kotlin-2-3-0-released/ [Accessed 27 April 2026].

   **Kotlin Coroutines for asynchronous operations:**
   Android Developers, 2024. Kotlin coroutines on Android. [Online] Available at: https://developer.android.com/kotlin/coroutines [Accessed 27 April 2026].

   **GitHub Actions for automated testing:**
   GitHub Actions, 2025. Automating builds and tests. [Online] Available at: https://docs.github.com/en/actions/tutorials/build-and-test-code [Accessed 27 April 2026].

   **SeekBar for budget goal sliders:**
   GeeksforGeeks, 2019. SeekBar in Kotlin. [Online] Available at: https://www.geeksforgeeks.org/kotlin/seekbar-in-kotlin/ [Accessed 28 April 2026].

   **LinearLayout for button navigation bar:**
   Android Developers, n.d. LinearLayout. [Online] Available at: https://developer.android.com/reference/android/widget/LinearLayout [Accessed 28 April 2026].

   **MaterialDatePicker for date range selection:**
    Android Developers, 2025. MaterialDatePicker.Builder API reference. [Online] Available at: https://developer.android.com/reference/com/google/android/material/datepicker/MaterialDatePicker.Builder [Accessed 28 April 2026].

   **Fragment transactions for screen navigation:**
    Android Developers, 2024. Fragments. [Online] Available at: https://developer.android.com/guide/fragments [Accessed 28 April 2026].

---

## License ⚖️

MIT License 
