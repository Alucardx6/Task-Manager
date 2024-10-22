package ir.abyx.task.data.remote.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    val id: String? = null,
    val email: String? = null,
    @SerializedName("displayName")
    val name: String? = null,
    val profilePicture: String? = null
)

fun UserModel.toFieldMap(): Map<String, String?> {
    val fieldMap = mutableMapOf<String, String?>()

    if (email != null) fieldMap["email"] = email
    if (name != null) fieldMap["displayName"] = name

    return fieldMap
}

data class PasswordModel(
    val newPassword: String,
    val oldPassword: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class VerifyRequest(
    val email: String,
    @SerializedName("verificationCode") val code: String
)

data class EmailRequest(
    val email: String
)

data class RegisterRequest(
    @SerializedName("displayName") val name: String,
    val email: String,
    val password: String
)

data class ResetPassword(
    val email: String,
    @SerializedName("resetCode") val verifyCode: String,
    @SerializedName("newPassword") val password: String
)
