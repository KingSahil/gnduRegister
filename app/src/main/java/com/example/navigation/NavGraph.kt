package com.example.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Main : Screen("main")
    object ManageStudents : Screen("manage_students")
    object AddEditStudent : Screen("add_edit_student/{studentId}") {
        fun createRoute(studentId: Long = -1L) = "add_edit_student/$studentId"
    }
    object About : Screen("about")
    object PdfPreview : Screen("pdf_preview/{date}/{semester}/{section}/{group}") {
        fun createRoute(
            date: String,
            semester: String,
            section: String,
            group: String
        ) = "pdf_preview/$date/$semester/$section/$group"
    }
}

sealed class BottomNavTab(
    val route: String,
    val title: String
) {
    object Attendance : BottomNavTab("attendance_tab", "Attendance")
    object Settings : BottomNavTab("settings_tab", "Settings")
}
