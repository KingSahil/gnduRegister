package com.example

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.navigation.Screen
import com.example.repository.ThemeMode
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AddEditStudentScreen
import com.example.ui.screens.MainScreen
import com.example.ui.screens.ManageStudentsScreen
import com.example.ui.screens.PdfPreviewScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.GnduRegisterTheme
import com.example.viewmodel.AttendanceViewModel
import com.example.viewmodel.ManageStudentsViewModel
import com.example.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {

    private val app by lazy { application as GnduApplication }

    private val attendanceViewModel: AttendanceViewModel by viewModels {
        AttendanceViewModel.Factory(app.attendanceRepository, app.subjectRepository)
    }

    private val manageStudentsViewModel: ManageStudentsViewModel by viewModels {
        ManageStudentsViewModel.Factory(app.studentRepository)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.Factory(app.settingsRepository, app.subjectRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            val themeMode by settingsViewModel.themeMode.collectAsState(initial = ThemeMode.SYSTEM)
            val dynamicColor by settingsViewModel.dynamicColor.collectAsState(initial = true)

            GnduRegisterTheme(
                themeMode = themeMode,
                dynamicColor = dynamicColor
            ) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Main.route
                ) {
                    composable(Screen.Splash.route) {
                        SplashScreen(
                            onSplashFinished = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Main.route) {
                        MainScreen(
                            attendanceViewModel = attendanceViewModel,
                            manageStudentsViewModel = manageStudentsViewModel,
                            settingsViewModel = settingsViewModel,
                            onNavigateToManageStudents = {
                                navController.navigate(Screen.ManageStudents.route)
                            },
                            onNavigateToAddStudent = {
                                navController.navigate(Screen.AddEditStudent.createRoute(-1L))
                            },
                            onNavigateToAbout = {
                                navController.navigate(Screen.About.route)
                            },
                            onNavigateToPdfPreview = { date, sem, sec, grp ->
                                navController.navigate(
                                    Screen.PdfPreview.createRoute(date, sem, sec, grp)
                                )
                            }
                        )
                    }

                    composable(Screen.ManageStudents.route) {
                        ManageStudentsScreen(
                            viewModel = manageStudentsViewModel,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToAddStudent = {
                                navController.navigate(Screen.AddEditStudent.createRoute(-1L))
                            },
                            onNavigateToEditStudent = { studentId ->
                                navController.navigate(Screen.AddEditStudent.createRoute(studentId))
                            }
                        )
                    }

                    composable(
                        route = Screen.AddEditStudent.route,
                        arguments = listOf(
                            navArgument("studentId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            }
                        )
                    ) { backStackEntry ->
                        val studentId = backStackEntry.arguments?.getLong("studentId") ?: -1L
                        AddEditStudentScreen(
                            viewModel = manageStudentsViewModel,
                            studentId = if (studentId > 0) studentId else null,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.About.route) {
                        AboutScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.PdfPreview.route,
                        arguments = listOf(
                            navArgument("date") { type = NavType.StringType },
                            navArgument("semester") { type = NavType.StringType },
                            navArgument("section") { type = NavType.StringType },
                            navArgument("group") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val date = backStackEntry.arguments?.getString("date") ?: ""
                        val semester = backStackEntry.arguments?.getString("semester") ?: ""
                        val section = backStackEntry.arguments?.getString("section") ?: ""
                        val group = backStackEntry.arguments?.getString("group") ?: ""

                        PdfPreviewScreen(
                            viewModel = attendanceViewModel,
                            date = date,
                            semester = semester,
                            section = section,
                            group = group,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
