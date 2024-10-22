package ir.abyx.task.ui.utils

import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import ir.huri.jcal.JalaliCalendar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.GregorianCalendar

object Utils {

    fun createImageMultipart(uri: Uri, key: String): MultipartBody.Part {
        val file = File(uri.path!!)

        val mimeType = when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "image/*"
        }

        val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(key, file.name, requestFile)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertToTehranTime(isoTime: String): String {

        val offsetDateTime = OffsetDateTime.parse(isoTime)

        val tehranZoneId = ZoneId.of("Asia/Tehran")
        val tehranTime = offsetDateTime.atZoneSameInstant(tehranZoneId)

        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        return tehranTime.format(timeFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun splitDateTime(dateTime: String): Pair<String, String> {
        val parts = dateTime.split("T")
        return parts[0].trim() to convertToTehranTime(dateTime)
    }

    fun splitDate(date: String): Triple<Int, Int, Int> {
        val dateParts = date.split("-")
        return Triple(dateParts[0].toInt(), dateParts[1].toInt(), dateParts[2].toInt())
    }

    fun jalaliToGregorian(jalaliDate: String, time: String): String {
        // Split the Jalali date into year, month, and day
        val dateParts = jalaliDate.split("-")
        val year = dateParts[0].toInt()
        val month = dateParts[1].toInt()
        val day = dateParts[2].toInt()

        // Convert Jalali to Gregorian using JalaliCalendar
        val jalaliCalendar = JalaliCalendar(year, month, day)
        val gregorianCalendar: GregorianCalendar = jalaliCalendar.toGregorian()

        // Extract the year, month, and day from the GregorianCalendar
        val gregorianYear = gregorianCalendar.get(Calendar.YEAR)
        val gregorianMonth = gregorianCalendar.get(Calendar.MONTH) + 1 // Months are zero-based
        val gregorianDay = gregorianCalendar.get(Calendar.DAY_OF_MONTH)

        // Format the Gregorian date as a string
        val gregorianDateString = "$gregorianYear-$gregorianMonth-$gregorianDay"

        // Append the time to the date
        return "${gregorianDateString}T${time}"
    }

    fun gregorianToJalali(gregorianDate: String): JalaliDate {
        // Split the Gregorian date into year, month, and day
        val dateParts = gregorianDate.split("-")
        val year = dateParts[0].toInt()
        val month = dateParts[1].toInt()
        val day = dateParts[2].toInt()

        // Create a GregorianCalendar instance
        val gregorianCalendar = GregorianCalendar(year, month - 1, day) // Months are zero-based

        // Convert Gregorian to Jalali using JalaliCalendar
        val jalaliCalendar = JalaliCalendar(gregorianCalendar)

        // Extract the year, month, and day from the JalaliCalendar
        val jalaliYear = jalaliCalendar.year
        val jalaliMonth = jalaliCalendar.month
        val jalaliDay = jalaliCalendar.day

        // Return a JalaliDate instance
        return JalaliDate(jalaliYear, jalaliMonth, jalaliDay)
    }


    fun getMonthStr(month: Int): String {
        return when (month) {
            1 -> "فروردین"
            2 -> "اردیبهشت"
            3 -> "خرداد"
            4 -> "تیر"
            5 -> "مرداد"
            6 -> "شهریور"
            7 -> "مهر"
            8 -> "آبان"
            9 -> "آذر"
            10 -> "دی"
            11 -> "بهمن"
            12 -> "اسفند"
            else -> "Invalid month"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateTimeDifference(
        endYear: Int, endMonth: Int, endDay: Int, endTime: String, startDateTime: LocalDateTime
    ): Pair<String, Float> {

        // Parse the end time
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        // Get the current date and time
        val currentDateTime = LocalDateTime.now()

        // Create LocalDateTime object for the end date with time
        val endDateTime = LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute)

        // If the current date/time is after the end date/time, return "Time's up" and 0 fraction
        if (currentDateTime.isAfter(endDateTime)) {
            return Pair("Time's up", 0f)
        }

        // Calculate the total countdown time in seconds (from start to end)
        val totalTimeInSeconds = ChronoUnit.SECONDS.between(startDateTime, endDateTime).toFloat()

        // Calculate the remaining time in seconds (from now to end)
        val remainingTimeInSeconds = ChronoUnit.SECONDS.between(currentDateTime, endDateTime).toFloat()

        // Calculate the time fraction (remaining time / total time)
        val timeFraction = remainingTimeInSeconds / totalTimeInSeconds

        // Create the human-readable time difference string (same as before)
        val totalYears = ChronoUnit.YEARS.between(currentDateTime, endDateTime)
        val currentDateTimeAfterYears = currentDateTime.plusYears(totalYears)

        val totalMonths = ChronoUnit.MONTHS.between(currentDateTimeAfterYears, endDateTime)
        val currentDateTimeAfterMonths = currentDateTimeAfterYears.plusMonths(totalMonths)

        val totalDays = ChronoUnit.DAYS.between(currentDateTimeAfterMonths, endDateTime)
        val currentDateTimeAfterDays = currentDateTimeAfterMonths.plusDays(totalDays)

        val totalHours = ChronoUnit.HOURS.between(currentDateTimeAfterDays, endDateTime)
        val currentDateTimeAfterHours = currentDateTimeAfterDays.plusHours(totalHours)

        val remainingMinutes = ChronoUnit.MINUTES.between(currentDateTimeAfterHours, endDateTime)

        val result = StringBuilder()
        if (totalYears > 0) result.append("$totalYears سال ")
        if (totalMonths > 0) result.append("$totalMonths ماه ")
        if (totalDays > 0) result.append("$totalDays روز ")
        if (totalHours > 0) result.append("$totalHours ساعت ")
        if (remainingMinutes > 0) result.append("$remainingMinutes دقیقه ")

        return Pair(result.toString().trim(), timeFraction)
    }
}