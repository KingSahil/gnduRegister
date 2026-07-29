# UI Architecture & Design System 🎨

The user interface of **GNDU Attendance Register** is implemented entirely using **Jetpack Compose** with **Material Design 3 (M3)** standards.

---

## 📱 Navigation Structure

Navigation is managed via Jetpack Compose Navigation (`NavHost`) hosted inside `MainActivity.kt`.

```
                        +--------------------+
                        |    SplashScreen    |
                        +---------+----------+
                                  | (1.2s timeout)
                                  v
                        +--------------------+
                        |     MainScreen     |
                        +----+----------+----+
                             |          |
              +--------------+          +--------------+
              | (Bottom Navigation)                     |
              v                                         v
   +--------------------+                    +-----------------------+
   |  AttendanceScreen  |                    | ManageStudentsScreen  |
   +---------+----------+                    +-----------+-----------+
             |                                           |
             | (Action: Print PDF)                       | (Action: Add/Edit)
             v                                           v
   +--------------------+                    +-----------------------+
   |  PdfPreviewScreen  |                    | AddEditStudentScreen  |
   +--------------------+                    +-----------------------+
             |                                           |
             +--------------------+----------------------+
                                  |
                                  v
                        +--------------------+
                        |    AboutScreen     |
                        +--------------------+
```

---

## 🎨 Material Design 3 Color Palette

Customized for GNDU branding with **GNDU Royal Blue** accents and accessible high-contrast indicators.

### Light Theme Scheme
- **Primary**: `Color(0xFF005BC1)` (Royal Blue)
- **Primary Container**: `Color(0xFFD8E2FF)`
- **Background**: `Color(0xFFF8F9FE)`
- **Surface**: `Color(0xFFFFFFFF)`
- **Present State**: `Color(0xFFE8F5E9)` background / `Color(0xFF2E7D32)` text
- **Absent State**: `Color(0xFFFFEBEE)` background / `Color(0xFFC62828)` text

### Dark Theme Scheme
- **Primary**: `Color(0xFFADC6FF)`
- **Primary Container**: `Color(0xFF00448E)`
- **Background**: `Color(0xFF111318)`
- **Surface**: `Color(0xFF1B1D24)`
- **Present State**: Dark Green Container / `Color(0xFF81C784)` text
- **Absent State**: Dark Red Container / `Color(0xFFE57373)` text

---

## 🧩 Key Composables

- **`StudentRow`**: Renders individual student cards with animated toggle states, status badges, roll numbers, and haptic feedback.
- **`SummaryCard`**: Displays live total present/absent count chips with percentage progress indicator.
- **`GnduLogo`**: Renders the official GNDU emblem from `res/drawable/gndu_logo.png`.
- **`PdfPreviewScreen`**: Live print preview of the generated attendance sheet before saving or printing.
