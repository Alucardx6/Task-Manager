package ir.abyx.task.data.remote.apiRepository

import ir.abyx.task.data.remote.model.MessageModel
import ir.abyx.task.data.remote.model.ProjectModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProjectApiService {

    @GET("projects")
    suspend fun getProjects(): Response<List<ProjectModel>>

    @POST("projects")
    suspend fun createProjects(
        @Body
        request: ProjectModel
    ): Response<MessageModel>

    @PUT("projects/{id}")
    suspend fun editProject(
        @Path("id") id: String,
        @Body request: ProjectModel
    ): Response<MessageModel>

    @DELETE("projects/{id}")
    suspend fun deleteProject(@Path("id") id: String): Response<MessageModel>
}