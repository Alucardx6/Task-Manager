package ir.abyx.task.data.remote

import ir.abyx.task.data.remote.apiRepository.AuthApiService
import ir.abyx.task.data.remote.apiRepository.ProjectApiService
import ir.abyx.task.data.remote.apiRepository.TaskApiService
import ir.abyx.task.data.remote.apiRepository.UserApiService
import ir.abyx.task.data.remote.ext.MyApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    private const val URL = "http://10.244.103.40:3000/api/"

    private val okHttpClient: OkHttpClient by lazy {
        val context = MyApplication.appContext
        if (context != null) {
            OkHttpClient.Builder()
                .cookieJar(AppCookieJar(context))
                .build()
        } else {
            OkHttpClient.Builder().build()
        }
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val userService: UserApiService by lazy {
        retrofit.create(UserApiService::class.java)
    }

    val projectService: ProjectApiService by lazy {
        retrofit.create(ProjectApiService::class.java)
    }

    val taskService: TaskApiService by lazy {
        retrofit.create(TaskApiService::class.java)
    }
}