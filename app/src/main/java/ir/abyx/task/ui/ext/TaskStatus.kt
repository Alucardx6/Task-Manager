package ir.abyx.task.ui.ext

import com.google.gson.annotations.SerializedName

enum class TaskStatus {
    @SerializedName("pending")
    TODO,
    @SerializedName("inProgress")
    IN_PROGRESS,
    @SerializedName("completed")
    DONE
}