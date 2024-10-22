package ir.abyx.task.ui.ext

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ReminderWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: "Reminder"
        val message = inputData.getString("message") ?: "It's time!"

        NotificationHelper.showNotification(applicationContext, title, message)
        return Result.success()
    }
}