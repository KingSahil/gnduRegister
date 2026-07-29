package com.example.ui.screens

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.navigation.BottomNavTab
import com.example.viewmodel.AttendanceViewModel
import com.example.viewmodel.ManageStudentsViewModel
import com.example.viewmodel.SettingsViewModel

@Composable
fun MainScreen(
    attendanceViewModel: AttendanceViewModel,
    manageStudentsViewModel: ManageStudentsViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateToManageStudents: () -> Unit,
    onNavigateToAddStudent: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToPdfPreview: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController: NavHostController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val tabs = listOf(
        BottomNavTab.Attendance,
        BottomNavTab.Settings
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                windowInsets = NavigationBarDefaults.windowInsets
            ) {
                tabs.forEach { tab ->
                    val selected = currentRoute == tab.route

                    val icon = when (tab) {
                        BottomNavTab.Attendance -> if (selected) Icons.Default.AssignmentTurnedIn else Icons.Outlined.AssignmentTurnedIn
                        BottomNavTab.Settings -> if (selected) Icons.Default.Settings else Icons.Outlined.Settings
                    }

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentRoute != tab.route) {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = tab.title
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        modifier = Modifier.testTag("bottom_nav_${tab.route}")
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavTab.Attendance.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                route = BottomNavTab.Attendance.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
            ) {
                AttendanceScreen(
                    viewModel = attendanceViewModel,
                    onNavigateToPdfPreview = onNavigateToPdfPreview
                )
            }

            composable(
                route = BottomNavTab.Settings.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { it }) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) + fadeOut() }
            ) {
                SettingsScreen(
                    viewModel = settingsViewModel,
                    onNavigateToManageStudents = onNavigateToManageStudents,
                    onNavigateToAddStudent = onNavigateToAddStudent,
                    onNavigateToAbout = onNavigateToAbout
                )
            }
        }
    }
}
