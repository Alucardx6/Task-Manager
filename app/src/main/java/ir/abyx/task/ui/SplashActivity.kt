package ir.abyx.task.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.abyx.task.R
import ir.abyx.task.data.remote.RetrofitService
import ir.abyx.task.ui.ext.ToastUtils
import ir.abyx.task.ui.theme.TaskTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Splash(
                        modifier = Modifier.padding(innerPadding),
                        this,
                        onResponse = {
                            if (it == 200)
                                startActivity(Intent(this, MainActivity::class.java))
                            else
                                startActivity(Intent(this, LoginSignup::class.java))

                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Splash(modifier: Modifier = Modifier, context: Context?, onResponse: (response: Int) -> Unit) {
    val service = RetrofitService.userService
    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getUser()
                onResponse(response.code())
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    ToastUtils.toastServerError(context!!)
                }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.white))
        ) {

            Column(
                modifier = modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 200.dp)
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = modifier.size(150.dp)
                )
                Text(
                    text = "تسک سنج",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.black)
                )
                Text(
                    modifier = modifier,
                    text = "با اپلیکیشن تسک سنج کارهاتو مدیریت کن!",
                    fontSize = 18.sp,
                    color = colorResource(id = R.color.black)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.ic_splash),
                contentDescription = "Bottom Image",
                modifier = modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(400.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashPreview() {
    TaskTheme {
        Splash(context = null, onResponse = {})
    }
}