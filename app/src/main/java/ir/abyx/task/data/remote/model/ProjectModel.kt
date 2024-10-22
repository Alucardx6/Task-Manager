package ir.abyx.task.data.remote.model

import com.google.gson.annotations.SerializedName

data class ProjectModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") var title: String,
    val owner: String? = null,
    val users: ArrayList<UsersModel>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val state: Boolean = false
)

data class UsersModel(
    val userId: String,
    val role: String
)
