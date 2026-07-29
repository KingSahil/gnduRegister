# System Architecture Overview 🏗️

The **GNDU Attendance Register (GnduRegister)** app is engineered following Android's official **Modern Android Architecture (MAD)** guidelines, utilizing **MVVM (Model-View-ViewModel)** with the **Repository Pattern** and **Unidirectional Data Flow (UDF)**.

---

## 🏛️ High-Level Architecture Diagram

```
+-----------------------------------------------------------------------+
|                               UI LAYER                                |
|   Jetpack Compose Screens (AttendanceScreen, ManageStudentsScreen, etc.) |
+-----------------------------------+-----------------------------------+
                                    | Observes StateFlow (UI State)
                                    v Emits User Actions (Events)
+-----------------------------------------------------------------------+
|                             VIEWMODEL LAYER                           |
|       AttendanceViewModel | ManageStudentsViewModel | SettingsViewModel|
+-----------------------------------+-----------------------------------+
                                    | Invokes Coroutines / Flows
                                    v
+-----------------------------------------------------------------------+
|                            REPOSITORY LAYER                           |
|    StudentRepository | AttendanceRepository | SubjectRepository       |
+-----------------------------------+-----------------------------------+
                                    | Executes Queries / Data Operations
                                    v
+-----------------------------------------------------------------------+
|                              DATA LAYER                               |
|                  Room Database (SQLite) + DataStore                   |
+-----------------------------------------------------------------------+
```

---

## 📐 Core Architecture Pillars

### 1. Unidirectional Data Flow (UDF)
- **State flows down**: ViewModels hold single sources of truth exposed via Kotlin `StateFlow` and `Flow`.
- **Events flow up**: Composables trigger user actions (e.g., `toggleAttendance()`, `updateFilter()`) directly on ViewModel methods.

### 2. Offline-First, Background Pre-Warming & Eager RAM Caching
- Data is stored locally in an embedded **SQLite Database** managed by **Room**.
- To prevent UI stutter or first-load latency on application startup, `GnduApplication` pre-warms the SQLite connection, pre-fetches student indexes, and pre-loads today's attendance data on `Dispatchers.IO` during `onCreate()`.
- StateFlow streams in `AttendanceViewModel` use `SharingStarted.Eagerly` so filtered student lists and count states are computed in memory before the UI renders.

### 3. Repository Pattern
- Repositories encapsulate data access logic and expose reactive Kotlin `Flow` sources to the ViewModels.
- Ensures strict separation between local database entities and UI consumer state.

---

## 🔄 Layer Breakdown

### 📱 UI Layer (`com.example.ui`)
- Built 100% in **Jetpack Compose**.
- Utilizes `collectAsStateWithLifecycle()` to safely consume ViewModel state flows without memory leaks.
- Uses `@Immutable` models (`StudentWithAttendance`) to optimize recomposition performance in `LazyColumn` item lists.

### 🧠 ViewModel Layer (`com.example.viewmodel`)
- `AttendanceViewModel`: Coordinates student lists, subject selection, filter states (semester, section, group, query), date selection, and attendance recording.
- `ManageStudentsViewModel`: Handles CRUD operations for students (Add, Edit, Delete, Bulk Clear).
- `SettingsViewModel`: Manages app theme preferences and subject configurations.

### 💾 Data & Repository Layer (`com.example.database` & `com.example.repository`)
- `AppDatabase`: Abstract RoomDatabase instance initialized as a thread-safe singleton.
- `StudentDao`: Handles queries with `LENGTH(rollNumber) ASC, rollNumber ASC` natural sorting and SQL indexing.
- `AttendanceDao`: Handles insertion and date-filtered queries for attendance records.
- `SubjectDao`: Manages subject class metadata.
