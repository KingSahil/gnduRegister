# UI Architecture & Design System 🎨

The user interface of **GNDU Attendance Register** is implemented entirely using **Jetpack Compose** with **Material Design 3 (M3)** standards.

---

## 📱 Navigation Structure & Motion Transitions

Navigation is managed via Jetpack Compose Navigation (`NavHost`) hosted inside `MainActivity.kt`. Bottom tab navigation between `AttendanceScreen` and `SettingsScreen` features directional horizontal slide and fade transitions (`slideInHorizontally` + `fadeIn`).

```
                        +--------------------+
                        |    SplashScreen    |
                        +---------+----------+
                                  | (Initial app launch)
                                  v
                        +--------------------+
                        |     MainScreen     |
                        +----+----------+----+
                             |          |
              +--------------+          +--------------+
              | (Bottom Navigation with Slide Anim)    |
              v                                         v
   +--------------------+                    +-----------------------+
   |  AttendanceScreen  | <================> |    SettingsScreen     |
   +---------+----------+                    +-----------+-----------+
             |                                           |
             | (Action: Print PDF)                       | (Action: Manage Students)
             v                                           v
   +--------------------+                    +-----------------------+
   |  PdfPreviewScreen  |                    | ManageStudentsScreen  |
   +--------------------+                    +-----------------------+
```

---

## 🎨 Material Design 3 Color Palette

Customized for GNDU branding with **GNDU Royal Blue** accents, transparent official GNDU emblem graphics, and accessible high-contrast indicators.

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

## 🧩 Key Composables & Layout Enhancements

- **`StudentRow`**: Renders individual student cards with animated toggle states, status badges, roll numbers, and haptic feedback in a virtualized `LazyColumn`.
- **`Sticky Search Bar`**: An oval search pill pinned in a Compose `stickyHeader` that remains fixed at the top while scrolling through attendance lists.
- **`FilterDropdownRow`**: Sequenced filter chips: **Subject** -> **Group** -> **Semester** -> **Section** for rapid class selection.
- **`SummaryCard`**: Displays live total present/absent count chips with percentage progress indicator.
- **`GnduLogo`**: Renders the transparent official Guru Nanak Dev University logo emblem from `res/drawable/gndu_logo.png` with clean vector fallback.
- **`PdfPreviewScreen`**: Live print preview of the generated attendance sheet before saving or printing.
