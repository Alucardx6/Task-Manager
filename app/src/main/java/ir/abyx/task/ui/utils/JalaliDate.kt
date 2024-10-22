package ir.abyx.task.ui.utils

data class JalaliDate(val year: Int, val month: Int, val day: Int) {
    val fullDate: String
        get() = "$year-$month-$day"
}
