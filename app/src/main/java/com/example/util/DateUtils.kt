package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dbDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())

    fun getTodayDbDate(): String {
        return dbDateFormat.format(Date())
    }

    fun formatDisplayDate(dbDate: String): String {
        return try {
            val date = dbDateFormat.parse(dbDate) ?: Date()
            displayDateFormat.format(date)
        } catch (e: Exception) {
            dbDate
        }
    }

    fun getDayOfWeek(dbDate: String): String {
        return try {
            val date = dbDateFormat.parse(dbDate) ?: Date()
            dayOfWeekFormat.format(date)
        } catch (e: Exception) {
            ""
        }
    }

    fun parseMillisToDbDate(millis: Long): String {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = millis
        }
        return dbDateFormat.format(calendar.time)
    }

    fun parseDbDateToMillis(dbDate: String): Long {
        return try {
            val date = dbDateFormat.parse(dbDate)
            date?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
