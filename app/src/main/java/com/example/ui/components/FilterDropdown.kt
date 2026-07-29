package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FilterDropdownRow(
    selectedSubject: String,
    subjects: List<String>,
    onSubjectSelected: (String) -> Unit,
    selectedSemester: String,
    semesters: List<String>,
    onSemesterSelected: (String) -> Unit,
    selectedSection: String,
    sections: List<String>,
    onSectionSelected: (String) -> Unit,
    selectedGroup: String,
    groups: List<String>,
    onGroupSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left-most: Subject / Class Filter Chip
        FilterChipMenu(
            label = selectedSubject,
            options = subjects,
            onOptionSelected = onSubjectSelected,
            testTag = "subject_filter_chip"
        )

        // Semester Filter Chip
        FilterChipMenu(
            label = selectedSemester,
            options = semesters,
            onOptionSelected = onSemesterSelected,
            testTag = "semester_filter_chip"
        )

        // Section Filter Chip
        FilterChipMenu(
            label = selectedSection,
            options = sections,
            onOptionSelected = onSectionSelected,
            testTag = "section_filter_chip"
        )

        // Group Filter Chip
        FilterChipMenu(
            label = selectedGroup,
            options = groups,
            onOptionSelected = onGroupSelected,
            testTag = "group_filter_chip"
        )
    }
}

@Composable
private fun FilterChipMenu(
    label: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        InputChip(
            selected = true,
            onClick = { expanded = true },
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    softWrap = false
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown"
                )
            },
            colors = InputChipDefaults.inputChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            border = null,
            modifier = Modifier.testTag(testTag)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
