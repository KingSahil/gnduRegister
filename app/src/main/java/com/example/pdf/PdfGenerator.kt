package com.example.pdf

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.model.StudentWithAttendance
import com.example.util.DateUtils
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {

    fun generatePdf(
        context: Context,
        date: String,
        subject: String = "",
        semester: String,
        section: String,
        group: String,
        students: List<StudentWithAttendance>
    ): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 width in points at 72 dpi
        val pageHeight = 842 // A4 height in points at 72 dpi

        val paint = Paint()
        val titlePaint = Paint()
        val headerPaint = Paint()
        val tableHeaderPaint = Paint()
        val rowPaint = Paint()

        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var yPos = 40f

        // Draw Header
        val primaryColor = Color.rgb(27, 94, 32)
        val headerBgPaint = Paint().apply {
            color = Color.rgb(240, 247, 240)
        }

        canvas.drawRect(RectF(20f, yPos, (pageWidth - 20).toFloat(), yPos + 60f), headerBgPaint)

        titlePaint.apply {
            color = primaryColor
            textSize = 18f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("GURU NANAK DEV UNIVERSITY", (pageWidth / 2).toFloat(), yPos + 26f, titlePaint)

        titlePaint.apply {
            color = Color.DKGRAY
            textSize = 13f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("ATTENDANCE REGISTER", (pageWidth / 2).toFloat(), yPos + 48f, titlePaint)

        yPos += 80f

        // Class Info Box
        headerPaint.apply {
            color = Color.BLACK
            textSize = 11f
            isFakeBoldText = true
        }

        val formattedDate = "${DateUtils.formatDisplayDate(date)} (${DateUtils.getDayOfWeek(date)})"
        val semText = if (semester.startsWith("Sem")) semester else "Sem $semester"
        val secText = if (section.startsWith("Section")) section else "Section $section"
        val groupText = if (group.isEmpty() || group == "All Groups" || group == "All") "All Groups" else group
        val subjectText = if (subject.isNotBlank()) subject else "N/A"

        canvas.drawText("Date: $formattedDate", 30f, yPos, headerPaint)
        canvas.drawText("Class: B.Tech CSE $semText", (pageWidth - 240).toFloat(), yPos, headerPaint)
        yPos += 18f
        canvas.drawText("Subject: $subjectText", 30f, yPos, headerPaint)
        canvas.drawText("Section: $secText   Group: $groupText", (pageWidth - 240).toFloat(), yPos, headerPaint)

        yPos += 30f

        val sortedStudents = students.sortedWith(
            compareByDescending<StudentWithAttendance> { it.isPresent }
                .thenBy { it.student.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
                .thenBy { it.student.rollNumber }
        )

        // Table Header
        val tableHeaderBg = Paint().apply { color = Color.rgb(230, 235, 240) }
        canvas.drawRect(RectF(30f, yPos, (pageWidth - 30).toFloat(), yPos + 25f), tableHeaderBg)

        tableHeaderPaint.apply {
            color = Color.BLACK
            textSize = 11f
            isFakeBoldText = true
        }

        canvas.drawText("Roll No.", 40f, yPos + 17f, tableHeaderPaint)
        canvas.drawText("Student Name", 140f, yPos + 17f, tableHeaderPaint)
        canvas.drawText("Status", (pageWidth - 100).toFloat(), yPos + 17f, tableHeaderPaint)

        yPos += 25f

        val linePaint = Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
        }

        rowPaint.apply {
            textSize = 10f
        }

        val presentPaint = Paint().apply {
            color = Color.rgb(46, 125, 50)
            textSize = 10f
            isFakeBoldText = true
        }

        val absentPaint = Paint().apply {
            color = Color.rgb(198, 40, 40)
            textSize = 10f
            isFakeBoldText = true
        }

        var presentCount = 0
        var absentCount = 0

        sortedStudents.forEachIndexed { index, item ->
            if (yPos > pageHeight - 80) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 40f

                // Redraw table header on new page
                canvas.drawRect(RectF(30f, yPos, (pageWidth - 30).toFloat(), yPos + 25f), tableHeaderBg)
                canvas.drawText("Roll No.", 40f, yPos + 17f, tableHeaderPaint)
                canvas.drawText("Student Name", 140f, yPos + 17f, tableHeaderPaint)
                canvas.drawText("Status", (pageWidth - 100).toFloat(), yPos + 17f, tableHeaderPaint)
                yPos += 25f
            }

            if (index % 2 == 1) {
                val rowBg = Paint().apply { color = Color.rgb(250, 250, 250) }
                canvas.drawRect(RectF(30f, yPos, (pageWidth - 30).toFloat(), yPos + 22f), rowBg)
            }

            canvas.drawText(item.student.rollNumber, 40f, yPos + 15f, rowPaint)
            canvas.drawText(item.student.name, 140f, yPos + 15f, rowPaint)

            if (item.isPresent) {
                presentCount++
                canvas.drawText("Present", (pageWidth - 100).toFloat(), yPos + 15f, presentPaint)
            } else {
                absentCount++
                canvas.drawText("Absent", (pageWidth - 100).toFloat(), yPos + 15f, absentPaint)
            }

            canvas.drawLine(30f, yPos + 22f, (pageWidth - 30).toFloat(), yPos + 22f, linePaint)
            yPos += 22f
        }

        // Summary Bar
        yPos += 15f
        if (yPos > pageHeight - 60) {
            pdfDocument.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = pdfDocument.startPage(pageInfo)
            canvas = page.canvas
            yPos = 40f
        }

        val summaryBg = Paint().apply { color = Color.rgb(240, 240, 240) }
        canvas.drawRect(RectF(30f, yPos, (pageWidth - 30).toFloat(), yPos + 35f), summaryBg)

        val summaryPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            isFakeBoldText = true
        }

        canvas.drawText("Total Present: $presentCount", 50f, yPos + 22f, presentPaint)
        canvas.drawText("Total Absent: $absentCount", (pageWidth - 180).toFloat(), yPos + 22f, absentPaint)

        pdfDocument.finishPage(page)

        // Save PDF to cache
        val pdfFile = File(context.cacheDir, "GNDU_Attendance_${date}_${semester}_${section}.pdf")
        try {
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.close()
            pdfDocument.close()
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
    }

    fun sharePdf(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share Attendance Register PDF"))
    }
}
