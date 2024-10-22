package ir.abyx.task.data.remote.ext

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.google.gson.Gson
import ir.abyx.task.data.remote.model.ErrorModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

object ErrorUtils {

    fun createCoroutineExceptionHandler(
        scope: CoroutineScope,
        snackBarHostState: SnackbarHostState,
        onLoadingFinished: () -> Unit
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            onLoadingFinished()
            when (exception) {
                is SocketTimeoutException -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Network request timed out. Please try again.",
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is IOException -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "Network error. Please check your connection.",
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                else -> {
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            message = "An unexpected error occurred. Please try again.",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
        }
    }

    fun getError(response: Response<*>): String {
        var error: String? = null
        val errorBody = response.errorBody()

        if (errorBody != null)
            error = Gson().fromJson(errorBody.string(), ErrorModel::class.java).message

        return error ?: "ارتباط با سرور برقرار نشد"
    }

}