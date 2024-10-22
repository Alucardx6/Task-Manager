package ir.abyx.task.data.remote.ext

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set

        val baseUrl: String by lazy {
            "http://10.244.103.40:3000"
        }
    }
}