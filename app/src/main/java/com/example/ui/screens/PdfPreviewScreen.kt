package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.StudentWithAttendance
import com.example.pdf.PdfGenerator
import com.example.ui.components.GnduLogo
import com.example.ui.theme.AbsentLightContent
import com.example.ui.theme.PresentLightContent
import com.example.util.DateUtils
import com.example.viewmodel.AttendanceViewModel
import java.io.File

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewScreen(
    viewModel: AttendanceViewModel,
    date: String,
    semester: String,
    section: String,
    group: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val selectedSubject by viewModel.selectedSubject.collectAsState()

    var exportList by remember { mutableStateOf<List<StudentWithAttendance>>(emptyList()) }
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val formattedDate = DateUtils.formatDisplayDate(date)
    val dayOfWeek = DateUtils.getDayOfWeek(date)

    LaunchedEffect(date, selectedSubject, semester, section, group) {
        isLoading = true
        val list = viewModel.getExportList()
        exportList = list
        val pdf = PdfGenerator.generatePdf(
            context = context,
            date = date,
            subject = selectedSubject,
            semester = semester,
            section = section,
            group = group,
            students = list
        )
        generatedPdfFile = pdf
        isLoading = false
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "PDF Preview",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("pdf_preview_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            generatedPdfFile?.let { file ->
                                PdfGenerator.sharePdf(context, file)
                            }
                        },
                        modifier = Modifier.testTag("pdf_share_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share PDF",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    generatedPdfFile?.let { file ->
                        PdfGenerator.sharePdf(context, file)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("pdf_fab_print_share")
            ) {
                Icon(
                    imageVector = Icons.Default.Print,
                    contentDescription = "Print / Export PDF"
                )
            }
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // PDF Document Preview Paper Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        // Header
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GnduLogo(size = 64.dp)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "GURU NANAK DEV UNIVERSITY",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "ATTENDANCE REGISTER",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Meta details
                        val semText = if (semester.startsWith("Sem")) semester else "Sem $semester"
                        val secText = if (section.startsWith("Section")) section else "Section $section"
                        val groupText = if (group.isEmpty() || group == "All Groups" || group == "All") "All Groups" else group

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Date: $formattedDate ($dayOfWeek)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                maxLines = 1,
                                softWrap = false
                            )
                            Text(
                                text = "Class: B.Tech CSE $semText",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Subject: ${if (selectedSubject.isNotBlank()) selectedSubject else "N/A"}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                softWrap = false
                            )
                            Text(
                                text = "Sec: $secText | Grp: $groupText",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider()

                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(vertical = 8.dp, horizontal = 8.dp)
                        ) {
                            Text(
                                text = "Roll No.",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                text = "Name",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Status",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.width(60.dp)
                            )
                        }

                        // Table Rows
                        val presentCount = exportList.count { it.isPresent }
                        val absentCount = exportList.count { !it.isPresent }

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            itemsIndexed(exportList) { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp, horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.student.rollNumber,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        modifier = Modifier.width(60.dp)
                                    )
                                    Text(
                                        text = item.student.name,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium
                                        ),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = if (item.isPresent) "Present" else "Absent",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = if (item.isPresent) PresentLightContent else AbsentLightContent,
                                        modifier = Modifier.width(60.dp)
                                    )
                                }
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                            }
                        }

                        // Table Footer Summary
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                        ) {
                            Text(
                                text = "Total Present: $presentCount",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PresentLightContent
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Total Absent: $absentCount",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AbsentLightContent
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
