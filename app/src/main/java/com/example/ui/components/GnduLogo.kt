package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun GnduLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val painter = runCatching { painterResource(id = R.drawable.gndu_logo) }.getOrNull()

    if (painter != null) {
        Image(
            painter = painter,
            contentDescription = "Guru Nanak Dev University Official Logo",
            modifier = modifier.size(size)
        )
    } else {
        val primaryColor = MaterialTheme.colorScheme.primary
        val goldColor = Color(0xFFFFD700)

        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(primaryColor)
                .border(2.dp, goldColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Guru Nanak Dev University Emblem",
                    tint = goldColor,
                    modifier = Modifier.size(size * 0.45f)
                )
                Text(
                    text = "GNDU",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = (size.value * 0.16).sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}




