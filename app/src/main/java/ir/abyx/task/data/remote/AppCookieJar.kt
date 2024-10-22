package ir.abyx.task.data.remote

import android.content.Context
import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class AppCookieJar(context: Context) : CookieJar {

    private val sharedPreferences =
        context.getSharedPreferences("MyAppCookies", Context.MODE_PRIVATE)

    // Save cookies
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        // Convert each cookie to its string representation
        val cookiesString = cookies.joinToString(";") { it.toString() }
        Log.d("AppCookieJar", "Saving cookies: $cookiesString")
        sharedPreferences.edit().putString("cookies", cookiesString).apply()
    }

    // Load cookies
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookiesString = sharedPreferences.getString("cookies", null)
        Log.d("AppCookieJar", "Loading cookies: $cookiesString")

        return cookiesString?.split(";")
            ?.mapNotNull { Cookie.parse(url, it.trim()) }
            ?: emptyList()
    }
}

