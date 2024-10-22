package ir.abyx.task.data.remote.model

import com.google.gson.annotations.SerializedName
import ir.abyx.task.ui.ext.TaskStatus

data class TaskModel(
    val id: String = "",
    val title: String,
    @SerializedName("description") val desc: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val startDatetime: String,
    val endDatetime: String,
    val taskWeight: String = "0",
    val progress: Int = 0,
    val users: List<String> = listOf(),
    val tags: List<String> = listOf(),
    val projectId: String
)
