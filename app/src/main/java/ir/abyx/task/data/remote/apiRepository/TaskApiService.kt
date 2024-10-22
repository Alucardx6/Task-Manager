package ir.abyx.task.data.remote.apiRepository

import ir.abyx.task.data.remote.model.MessageModel
import ir.abyx.task.data.remote.model.TaskModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface TaskApiService {

    @GET("projects/{id}/tasks")
    suspend fun getTasks(@Path("id") id: String): Response<List<TaskModel>>

    @POST("projects/{id}/tasks")
    suspend fun createTask(
        @Path("id") id: String,
        @Body request: TaskModel
    ): Response<MessageModel>

    @PATCH("projects/{projectId}/tasks/{taskId}")
    suspend fun updateTask(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String,
        @Body request: TaskModel
    ): Response<MessageModel>

    @DELETE("projects/{projectId}/tasks/{taskId}")
    suspend fun deleteTask(
        @Path("projectId") projectId: String,
        @Path("taskId") taskId: String
    ): Response<MessageModel>

}