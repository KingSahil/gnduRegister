package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pdf.PdfGenerator
import com.example.ui.components.AttendanceStudentRow
import com.example.ui.components.AttendanceSummaryCards
import com.example.ui.components.FilterDropdownRow
import com.example.util.DateUtils
import com.example.viewmodel.AttendanceViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    onNavigateToPdfPreview: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedSubject by viewModel.selectedSubject.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val selectedSemester by viewModel.selectedSemester.collectAsState()
    val selectedSection by viewModel.selectedSection.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val studentsList by viewModel.studentsWithAttendance.collectAsState()
    val presentCount by viewModel.presentCount.collectAsState()
    val absentCount by viewModel.absentCount.collectAsState()
    val historyDates by viewModel.historyDates.collectAsState()

    val onToggleAttendance = remember(viewModel) {
        { item: com.example.model.StudentWithAttendance ->
            viewModel.toggleStudentAttendance(item)
        }
    }

    var showHistoryDialog by remember { mutableStateOf(false) }

    // Date Picker Dialog setup
    val calendar = remember(selectedDate) {
        Calendar.getInstance().apply {
            timeInMillis = DateUtils.parseDbDateToMillis(selectedDate)
        }
    }

    val datePickerDialog = remember(context, selectedDate) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val cal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                viewModel.setSelectedDate(DateUtils.parseMillisToDbDate(cal.timeInMillis))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Attendance",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                actions = {
                    // PDF Export Button
                    IconButton(
                        onClick = {
                            onNavigateToPdfPreview(
                                selectedDate,
                                selectedSemester,
                                selectedSection,
                                selectedGroup
                            )
                        },
                        modifier = Modifier.testTag("export_pdf_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "Export PDF Report",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Attendance History Button
                    IconButton(
                        onClick = { showHistoryDialog = true },
                        modifier = Modifier.testTag("attendance_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Attendance History",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .testTag("attendance_student_list"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Dropdown Filters Row
            item {
                FilterDropdownRow(
                    selectedSubject = selectedSubject,
                    subjects = subjects,
                    onSubjectSelected = { viewModel.setSelectedSubject(it) },
                    selectedSemester = selectedSemester,
                    semesters = viewModel.semesters,
                    onSemesterSelected = { viewModel.setSelectedSemester(it) },
                    selectedSection = selectedSection,
                    sections = viewModel.sections,
                    onSectionSelected = { viewModel.setSelectedSection(it) },
                    selectedGroup = selectedGroup,
                    groups = viewModel.groups,
                    onGroupSelected = { viewModel.setSelectedGroup(it) }
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Compact Date Picker + Summary Cards merged in 1 horizontal line
            item {
                AttendanceSummaryCards(
                    selectedDate = selectedDate,
                    onOpenDatePicker = { datePickerDialog.show() },
                    presentCount = presentCount,
                    absentCount = absentCount
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Sticky Search Bar pinned to top when scrolling (Oval / YouTube Style Pill)
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("attendance_search_input"),
                            placeholder = { 
                                Text(
                                    "Search student...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
                                ) 
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search Icon",
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = { viewModel.setSearchQuery("") },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Search",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(50.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedBorderColor = Color.Transparent,
                                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }

            // Tap Instruction Prompt
            item {
                Text(
                    text = "Tap on a student to mark Present / Absent",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                )
            }

            // Student Attendance List
            if (studentsList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No students found for selected filters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                items(
                    items = studentsList,
                    key = { it.student.id },
                    contentType = { "student_attendance_row" }
                ) { item ->
                    AttendanceStudentRow(
                        item = item,
                        onTap = onToggleAttendance,
                        modifier = Modifier.padding(vertical = 2.5.dp)
                    )
                }
            }
        }
    }

    // Attendance History Dialog
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Text(
                    text = "Attendance History",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Select a date to open and edit attendance:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (historyDates.isEmpty()) {
                        Text(
                            text = "No attendance history records available.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.height(240.dp)) {
                            items(historyDates) { dateStr ->
                                TextButton(
                                    onClick = {
                                        viewModel.setSelectedDate(dateStr)
                                        showHistoryDialog = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${DateUtils.formatDisplayDate(dateStr)} (${DateUtils.getDayOfWeek(dateStr)})",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showHistoryDialog = false
                    datePickerDialog.show()
                }) {
                    Text("Pick Date")
                }
            },
            dismissButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

