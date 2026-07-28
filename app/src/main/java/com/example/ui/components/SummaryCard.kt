package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AbsentDarkBg
import com.example.ui.theme.AbsentDarkContent
import com.example.ui.theme.AbsentLightBg
import com.example.ui.theme.AbsentLightContent
import com.example.ui.theme.PresentDarkBg
import com.example.ui.theme.PresentDarkContent
import com.example.ui.theme.PresentLightBg
import com.example.ui.theme.PresentLightContent
import com.example.util.DateUtils

@Composable
fun AttendanceSummaryCards(
    selectedDate: String,
    onOpenDatePicker: () -> Unit,
    presentCount: Int,
    absentCount: Int,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val presentBg = if (isDark) PresentDarkBg else PresentLightBg
    val presentText = if (isDark) PresentDarkContent else PresentLightContent

    val absentBg = if (isDark) AbsentDarkBg else AbsentLightBg
    val absentText = if (isDark) AbsentDarkContent else AbsentLightContent

    val formattedDate = DateUtils.formatDisplayDate(selectedDate)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Date Picker Compact Card
        Card(
            modifier = Modifier
                .weight(1.3f)
                .testTag("date_picker_card")
                .clickable { onOpenDatePicker() },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Pick Date",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }

        // Present Card
        Card(
            modifier = Modifier
                .weight(1f)
                .testTag("present_summary_card"),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = presentBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Present",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = presentText,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$presentCount",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = presentText
                )
            }
        }

        // Absent Card
        Card(
            modifier = Modifier
                .weight(1f)
                .testTag("absent_summary_card"),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = absentBg)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Absent",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = absentText,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$absentCount",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = absentText
                )
            }
        }
    }
}

