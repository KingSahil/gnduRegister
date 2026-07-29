package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GnduLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    val textMeasurer = rememberTextMeasurer()

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h * 0.46f)
            val outerRadius = w * 0.44f

            // Colors matching original emblem
            val goldYellow = Color(0xFFFFCC00)
            val borderYellow = Color(0xFFF5B000)
            val skyBlue = Color(0xFF008CCB)
            val darkNavy = Color(0xFF003D75)
            val flameOrange = Color(0xFFFF9500)
            val white = Color(0xFFFFFFFF)

            // 1. Outer Golden Ring
            drawCircle(
                color = borderYellow,
                radius = outerRadius,
                center = center
            )

            // 2. Main Blue Circular Seal
            drawCircle(
                color = skyBlue,
                radius = outerRadius * 0.92f,
                center = center
            )

            // 3. Central Temple / Archway Structure
            val archWidth = outerRadius * 0.85f
            val archHeight = outerRadius * 1.15f
            val archLeft = center.x - archWidth / 2f
            val archTop = center.y - archHeight / 2f + h * 0.02f

            // Yellow Arch background & outline
            val archPath = Path().apply {
                moveTo(archLeft, archTop + archHeight)
                lineTo(archLeft, archTop + archHeight * 0.4f)
                // Scalloped / pointed temple dome top
                quadraticTo(archLeft, archTop + archHeight * 0.15f, center.x, archTop)
                quadraticTo(archLeft + archWidth, archTop + archHeight * 0.15f, archLeft + archWidth, archTop + archHeight * 0.4f)
                lineTo(archLeft + archWidth, archTop + archHeight)
                close()
            }

            drawPath(
                path = archPath,
                color = goldYellow,
                style = Stroke(width = w * 0.025f)
            )

            // Inner dark blue arch cavity
            val innerArchPath = Path().apply {
                val pad = w * 0.02f
                moveTo(archLeft + pad, archTop + archHeight - pad)
                lineTo(archLeft + pad, archTop + archHeight * 0.42f)
                quadraticTo(archLeft + pad, archTop + archHeight * 0.18f, center.x, archTop + pad * 1.5f)
                quadraticTo(archLeft + archWidth - pad, archTop + archHeight * 0.18f, archLeft + archWidth - pad, archTop + archHeight * 0.42f)
                lineTo(archLeft + archWidth - pad, archTop + archHeight - pad)
                close()
            }

            drawPath(
                path = innerArchPath,
                color = darkNavy
            )

            // 4. Illuminated Lamp Flame & Rays inside arch
            val flameCenterY = archTop + archHeight * 0.35f
            
            // Rays from flame
            for (i in -4..4) {
                val angle = Math.toRadians(i * 20.0 - 90.0)
                val rayStart = Offset(center.x + Math.cos(angle).toFloat() * w * 0.04f, flameCenterY + Math.sin(angle).toFloat() * h * 0.04f)
                val rayEnd = Offset(center.x + Math.cos(angle).toFloat() * w * 0.12f, flameCenterY + Math.sin(angle).toFloat() * h * 0.12f)
                drawLine(
                    color = white,
                    start = rayStart,
                    end = rayEnd,
                    strokeWidth = w * 0.015f,
                    cap = StrokeCap.Round
                )
            }

            // Lamp Flame
            val flamePath = Path().apply {
                moveTo(center.x, flameCenterY - h * 0.09f)
                quadraticTo(center.x + w * 0.04f, flameCenterY - h * 0.02f, center.x, flameCenterY + h * 0.04f)
                quadraticTo(center.x - w * 0.04f, flameCenterY - h * 0.02f, center.x, flameCenterY - h * 0.09f)
                close()
            }
            drawPath(path = flamePath, color = flameOrange)

            // Lamp base stand
            drawLine(
                color = goldYellow,
                start = Offset(center.x, flameCenterY + h * 0.04f),
                end = Offset(center.x, flameCenterY + h * 0.12f),
                strokeWidth = w * 0.025f
            )

            // 5. Open Book at Arch Base
            val bookWidth = archWidth * 0.82f
            val bookHeight = archHeight * 0.22f
            val bookTop = archTop + archHeight * 0.72f
            
            val bookPath = Path().apply {
                moveTo(center.x - bookWidth / 2f, bookTop)
                lineTo(center.x + bookWidth / 2f, bookTop)
                lineTo(center.x + bookWidth / 2f, bookTop + bookHeight)
                lineTo(center.x - bookWidth / 2f, bookTop + bookHeight)
                close()
            }
            drawPath(path = bookPath, color = white)
            drawPath(path = bookPath, color = goldYellow, style = Stroke(width = w * 0.015f))

            // 6. Wheat Ears Flanking Left and Right
            for (side in listOf(-1f, 1f)) {
                val earX = center.x + side * (outerRadius * 0.65f)
                val earY = center.y + h * 0.05f
                for (j in 0..4) {
                    val offsetY = j * (h * 0.035f)
                    drawCircle(
                        color = goldYellow,
                        radius = w * 0.022f,
                        center = Offset(earX + side * (j % 2 * w * 0.015f), earY - offsetY)
                    )
                }
            }

            // 7. Bottom University Banner Ribbon
            val bannerWidth = w * 0.82f
            val bannerHeight = h * 0.22f
            val bannerTop = h * 0.74f
            val bannerLeft = (w - bannerWidth) / 2f

            val bannerPath = Path().apply {
                moveTo(bannerLeft, bannerTop)
                lineTo(bannerLeft + bannerWidth, bannerTop)
                lineTo(bannerLeft + bannerWidth * 0.92f, bannerTop + bannerHeight)
                lineTo(bannerLeft + bannerWidth * 0.08f, bannerTop + bannerHeight)
                close()
            }

            drawPath(path = bannerPath, color = darkNavy)
            drawPath(path = bannerPath, color = borderYellow, style = Stroke(width = w * 0.02f))

            // Text on banner
            val textResult = textMeasurer.measure(
                text = "GURU NANAK DEV UNIVERSITY",
                style = TextStyle(
                    color = white,
                    fontSize = (size.value * 0.06f).sp,
                    fontWeight = FontWeight.Bold
                )
            )
            drawText(
                textLayoutResult = textResult,
                topLeft = Offset(
                    center.x - textResult.size.width / 2f,
                    bannerTop + (bannerHeight - textResult.size.height) / 2f
                )
            )
        }
    }
}

