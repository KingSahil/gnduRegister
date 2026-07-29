# GNDU Attendance Register (GnduRegister) 🎓📱

An offline-first, high-performance Android attendance management application custom-built for **Guru Nanak Dev University (GNDU), Amritsar**. Designed using modern Android development practices with **Kotlin**, **Jetpack Compose**, **Room Database**, and **Material Design 3**.

---

## 🌟 Key Features

- **⚡ Instant Student Attendance**: Toggle attendance (Present/Absent) with single tap & subtle haptic feedback.
- **🏷️ Flexible Class Filtering**: Filter students instantly by Semester, Section, Group, or search by Name/Roll Number.
- **📚 Multi-Subject Management**: Add, edit, or delete custom subjects with assigned default credits/hours.
- **📅 Attendance History & Editing**: Select any date to view or modify past attendance records easily.
- **📄 PDF Export & Printing**: Generate official GNDU attendance register sheets with GNDU logo branding, roll call tables, and summary counts ready to print or share.
- **🎨 Modern Material 3 Theming**: Light/Dark theme support with GNDU Royal Blue accents and optimized contrast colors for Present (`#E8F5E9`) and Absent (`#FFEBEE`) states.
- **⚡ Pre-warmed Offline Persistence**: Database pre-warming on application startup ensures smooth 60fps scrolling and instant list rendering.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (`androidx.compose.material3`)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern + Clean Data Flow
- **Local Database**: [Room Database](https://developer.android.com/training/data-storage/room) with SQLite indexing
- **Asynchronous / Reactive Flow**: Kotlin Coroutines & `StateFlow`
- **PDF Generation**: Native Android `PdfDocument` & Canvas API with GNDU official seal branding

---

## 📂 Project Structure

```
├── app/
│   └── src/main/java/com/example/
│       ├── GnduApplication.kt       # Application class & DB pre-warming
│       ├── MainActivity.kt          # Single-activity navigation host
│       ├── database/                # Room DB, DAOs, Entity schemas, Seed Data
│       ├── model/                   # Data domain models & UI state wrappers
│       ├── navigation/              # Type-safe Jetpack Compose Navigation
│       ├── pdf/                     # Attendance PDF document generator
│       ├── repository/              # Data repositories (Student, Subject, Attendance, Settings)
│       ├── ui/                      # Jetpack Compose UI
│       │   ├── components/          # Reusable UI widgets (SummaryCard, StudentRow, GnduLogo)
│       │   ├── screens/             # App screens (Attendance, Manage, PDF Preview, About, Settings)
│       │   └── theme/               # Color schemes, Typography, M3 Theme setup
│       ├── util/                    # Date utilities & helper functions
│       └── viewmodel/               # Architecture ViewModels
├── docs/                            # Comprehensive Architecture Documentation
│   ├── ARCHITECTURE.md              # System Architecture & Flow
│   ├── DATA_MODEL.md                # Room Schema & Entity Specifications
│   └── UI_ARCHITECTURE.md           # Compose Navigation & UI Design System
└── README.md
```

---

## 📘 Architecture Documentation

For in-depth explanations of the system design and implementation details, refer to the `/docs/` folder:

- [System Architecture Overview](docs/ARCHITECTURE.md)
- [Database Schema & Data Model](docs/DATA_MODEL.md)
- [UI Architecture & Design System](docs/UI_ARCHITECTURE.md)

---

## 🚀 Building & Running

1. Clone the repository.
2. Open the project in **Android Studio (Ladybug / Jellyfish or newer)**.
3. Gradle sync will automatically download dependencies defined in `build.gradle.kts` and `settings.gradle.kts`.
4. Select `app` run configuration and click **Run** (or press `Shift + F10`) on an emulator or Android device (Android 8.0+ / API 26+).

---

## 👨‍💻 Developer & Credits

Developed with ❤️ by **Sahil**  
*B.Tech Computer Science & Engineering Student*  
**Guru Nanak Dev University, Amritsar**

- **Portfolio**: [sahilfolio.tech](https://sahilfolio.tech)
- **GitHub**: [@kingsahil](https://www.github.com/kingsahil)
- **Instagram**: [@supreme__sahil](https://www.instagram.com/supreme__sahil)
