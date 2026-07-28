package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GnduLogo(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val outerRadius = w / 2f - 4f

            // Outer golden ring
            drawCircle(
                color = Color(0xFFE5A93C),
                radius = outerRadius,
                center = center,
                style = Stroke(width = w * 0.04f)
            )

            // Inner dark blue filled seal background
            drawCircle(
                color = Color(0xFF0F3260),
                radius = outerRadius - w * 0.03f,
                center = center
            )

            // Inner golden dotted border
            drawCircle(
                color = Color(0xFFF9C74F),
                radius = outerRadius - w * 0.12f,
                center = center,
                style = Stroke(width = w * 0.02f)
            )

            // Center shield / book icon
            val bookPath = Path().apply {
                val bw = w * 0.4f
                val bh = h * 0.28f
                val left = center.x - bw / 2f
                val top = center.y - bh / 2f + h * 0.05f

                moveTo(center.x, top)
                cubicTo(center.x - bw * 0.2f, top - bh * 0.1f, left, top + bh * 0.1f, left, top + bh * 0.2f)
                lineTo(left, top + bh)
                cubicTo(left, top + bh * 0.8f, center.x - bw * 0.2f, top + bh * 0.7f, center.x, top + bh * 0.8f)
                cubicTo(center.x + bw * 0.2f, top + bh * 0.7f, left + bw, top + bh * 0.8f, left + bw, top + bh)
                lineTo(left + bw, top + bh * 0.2f)
                cubicTo(left + bw, top + bh * 0.1f, center.x + bw * 0.2f, top - bh * 0.1f, center.x, top)
                close()
            }

            drawPath(
                path = bookPath,
                color = Color(0xFFF9C74F)
            )

            // Lamp flame on top of book
            val flamePath = Path().apply {
                val topY = center.y - h * 0.28f
                moveTo(center.x, topY)
                quadraticTo(center.x + w * 0.08f, topY + h * 0.08f, center.x, topY + h * 0.14f)
                quadraticTo(center.x - w * 0.08f, topY + h * 0.08f, center.x, topY)
                close()
            }

            drawPath(
                path = flamePath,
                color = Color(0xFFF39C12)
            )

            // Base stand arc
            drawArc(
                color = Color(0xFFE5A93C),
                startAngle = 45f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(center.x - outerRadius * 0.7f, center.y - outerRadius * 0.7f),
                size = Size(outerRadius * 1.4f, outerRadius * 1.4f),
                style = Stroke(width = w * 0.035f)
            )
        }
    }
}
