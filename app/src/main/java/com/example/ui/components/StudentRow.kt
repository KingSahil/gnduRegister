package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.luminance
import com.example.model.StudentWithAttendance
import com.example.ui.theme.AbsentDarkBg
import com.example.ui.theme.AbsentDarkContent
import com.example.ui.theme.AbsentLightBg
import com.example.ui.theme.AbsentLightContent
import com.example.ui.theme.PresentDarkBg
import com.example.ui.theme.PresentDarkContent
import com.example.ui.theme.PresentLightBg
import com.example.ui.theme.PresentLightContent

private val ItemCornerShape = RoundedCornerShape(10.dp)

@Composable
fun AttendanceStudentRow(
    item: StudentWithAttendance,
    onTap: (StudentWithAttendance) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val bgScheme = MaterialTheme.colorScheme.background
    val isDark = remember(bgScheme) { bgScheme.luminance() < 0.5f }

    val (bgColor, textColor) = remember(item.isPresent, isDark) {
        if (item.isPresent) {
            if (isDark) PresentDarkBg to PresentDarkContent else PresentLightBg to PresentLightContent
        } else {
            if (isDark) AbsentDarkBg to AbsentDarkContent else AbsentLightBg to AbsentLightContent
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("student_row_${item.student.rollNumber}")
            .clip(ItemCornerShape)
            .background(bgColor)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onTap(item)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Roll Number
            Text(
                text = item.student.rollNumber,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = textColor,
                modifier = Modifier.width(52.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Student Name
            Text(
                text = item.student.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


