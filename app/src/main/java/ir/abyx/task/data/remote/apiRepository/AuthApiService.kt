package ir.abyx.task.data.remote.apiRepository

import ir.abyx.task.data.remote.model.EmailRequest
import ir.abyx.task.data.remote.model.LoginRequest
import ir.abyx.task.data.remote.model.MessageModel
import ir.abyx.task.data.remote.model.RegisterRequest
import ir.abyx.task.data.remote.model.ResetPassword
import ir.abyx.task.data.remote.model.VerifyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun loginUser(
        @Body request: LoginRequest
    ): Response<MessageModel>

    @POST("auth/register")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<MessageModel>

    @POST("auth/verify")
    suspend fun verifyUser(
        @Body request: VerifyRequest
    ): Response<MessageModel>

    @POST("auth/resend-verification")
    suspend fun resendVerification(
        @Body request: EmailRequest
    ): Response<MessageModel>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: EmailRequest
    ): Response<MessageModel>

    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPassword
    ): Response<MessageModel>

    @POST("auth/logout")
    suspend fun logout(): Response<MessageModel>
}