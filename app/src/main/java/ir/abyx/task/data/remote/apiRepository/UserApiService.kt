package ir.abyx.task.data.remote.apiRepository

import ir.abyx.task.data.remote.model.MessageModel
import ir.abyx.task.data.remote.model.PasswordModel
import ir.abyx.task.data.remote.model.UserModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part

interface UserApiService {

    @GET("users/me")
    suspend fun getUser(): Response<UserModel>

    //    @FormUrlEncoded
    @PATCH("users/me")
    suspend fun updateUser(
        @Body
        request: UserModel

//        @FieldMap fields: Map<String, String?>
    ): Response<MessageModel>

    @Multipart
    @PATCH("users/me")
    suspend fun updateUserProfilePicture(
        @Part profilePicture: MultipartBody.Part
    ): Response<MessageModel>

    @PATCH("users/me/password")
    suspend fun updatePassword(
        @Body
        request: PasswordModel
    ): Response<MessageModel>
}