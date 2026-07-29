# Database Schema & Data Model 📊

The **GNDU Attendance Register** uses **Room** (SQLite) for persistent local storage.

---

## 🗄️ Database Schema ERD

```
+-----------------------------------+        +-----------------------------------+
|             students              |        |        attendance_records         |
+-----------------------------------+        +-----------------------------------+
| id: Long (PK, AutoGen)           |<-------+| id: Long (PK, AutoGen)            |
| name: String                      |        | studentId: Long (FK -> students)  |
| rollNumber: String (Unique Index) |        | date: String (YYYY-MM-DD)         |
| semester: String                  |        | subjectId: Long                   |
| section: String                   |        | isPresent: Boolean                |
| group: String                     |        +-----------------------------------+
+-----------------------------------+
                  ^ (Composite Index: semester, section, group)

+-----------------------------------+
|          subject_classes          |
+-----------------------------------+
| id: Long (PK, AutoGen)            |
| name: String                      |
| code: String                      |
+-----------------------------------+
```

---

## 📋 Entity Specifications

### 1. `Student` Entity (`students` table)
Represents a student enrolled in a specific class section.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | Primary Key, AutoGenerate | Unique ID |
| `name` | `String` | NOT NULL | Full name of student |
| `rollNumber` | `String` | Unique Index | University roll number (e.g. "101") |
| `semester` | `String` | Indexed | Semester (e.g. "3", "5") |
| `section` | `String` | Indexed | Section (e.g. "A", "B") |
| `group` | `String` | Indexed | Lab group (e.g. "G1", "G2") |

*Indexes*:
- `Index("rollNumber", unique = true)`
- `Index("semester", "section", "group")` for fast query filtering.

---

### 2. `AttendanceRecord` Entity (`attendance_records` table)
Stores daily attendance entries per student per subject.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | Primary Key, AutoGenerate | Unique record ID |
| `studentId` | `Long` | Foreign Key (`students.id`, CASCADE) | References student |
| `date` | `String` | Indexed | ISO Date string (`YYYY-MM-DD`) |
| `subjectId` | `Long` | Indexed | ID of the subject |
| `isPresent` | `Boolean` | NOT NULL | `true` if Present, `false` if Absent |

---

### 3. `SubjectClass` Entity (`subject_classes` table)
Stores subject catalog entries.

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `Long` | Primary Key, AutoGenerate | Subject ID |
| `name` | `String` | NOT NULL | Subject Title (e.g. "Database Management") |
| `code` | `String` | NOT NULL | Subject Code (e.g. "CS-301") |

---

## 🚀 Optimized SQL Queries

```sql
-- Filtered student list with natural numeric roll number sorting
SELECT * FROM students 
WHERE (:semester = '' OR semester = :semester)
  AND (:section = '' OR section = :section)
  AND (:group = '' OR `group` = :group)
  AND (:query = '' OR rollNumber LIKE '%' || :query || '%' OR LOWER(name) LIKE '%' || LOWER(:query) || '%')
ORDER BY LENGTH(rollNumber) ASC, rollNumber ASC;
```
