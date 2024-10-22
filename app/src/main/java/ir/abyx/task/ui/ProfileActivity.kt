package ir.abyx.task.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.yalantis.ucrop.UCrop
import ir.abyx.task.R
import ir.abyx.task.data.remote.RetrofitService
import ir.abyx.task.data.remote.ext.MyApplication
import ir.abyx.task.ui.customView.CustomViews
import ir.abyx.task.ui.ext.SettingType
import ir.abyx.task.ui.ext.ToastUtils
import ir.abyx.task.ui.ui.theme.TaskTheme
import ir.abyx.task.ui.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskTheme {
                Profile()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Profile() {
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val cropImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            resultUri?.let {
                selectedImageUri = it

                CoroutineScope(Dispatchers.IO).launch {
                    val imagePart = Utils.createImageMultipart(it, "profilePicture")

                    val response = RetrofitService.userService.updateUserProfilePicture(imagePart)

                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            ToastUtils.toast(context, "عکس پروفایل با موفقیت آپدیت شد")
                        } else {
                            ToastUtils.toast(context, "مشکلی در آپلود عکس پیش‌آمده")
                        }
                    }
                }
            }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val destinationUri = Uri.fromFile(File(context.cacheDir, "croppedImage.jpg"))
            val uCropIntent = UCrop.of(it, destinationUri)
                .withAspectRatio(1f, 1f)
                .withMaxResultSize(400, 400)
                .getIntent(context)
            cropImageLauncher.launch(uCropIntent)
        }
    }

    var name by remember { mutableStateOf("کاربر") }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitService.userService.getUser()

            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    name = data.name!!
                    selectedImageUri = data.profilePicture?.toUri()
                }
            } else {
                ToastUtils.toast(context, "مشکلی پیش آمده، مجدد تلاش کنید")
            }
        }
    }


    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
            ) {
                selectedImageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(MyApplication.baseUrl + it),
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                } ?: CustomViews.UserProfileImage(
                    name = name,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.5f),
                                    Color.Transparent
                                )
                            )
                        )
                        .align(Alignment.TopCenter)
                )

                // Bottom shadow gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f)
                                )
                            )
                        )
                        .align(Alignment.BottomCenter)
                )

                Image(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.TopEnd)
                        .clipToBounds()
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                        .padding(start = 16.dp)
                        .clickable {
                            (context as? ComponentActivity)?.finish()
                        },
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "close"
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                ) {
                    Text(
                        text = name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(y = (30).dp)
                        .padding(end = 8.dp)
                        .size(60.dp)
                        .clickable {
                            imagePickerLauncher.launch("image/*")
                        },
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    shape = CircleShape,
                    color = Color.White
                )
                {
                    Image(
                        modifier = Modifier.padding(12.dp),
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "camera"
                    )
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Column(
                Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            ) {
                ProfileDetails(icon = R.drawable.ic_camera, title = "اکانت") {
                    val intent = Intent(context, ProfileSettingActivity::class.java)
                    intent.putExtra("type", SettingType.Profile.ordinal)
                    intent.putExtra("name", name)
//                    intent.putExtra("username", userId)
                    context.startActivity(intent)
                }
                ProfileDetails(icon = R.drawable.ic_notifications, title = "اعلانات") {
                    val intent = Intent(context, ProfileSettingActivity::class.java)
                    intent.putExtra("type", SettingType.Notification.ordinal)
                    context.startActivity(intent)
                }

                Spacer(
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                        .background(color = Color.LightGray)
                        .height(0.7.dp)
                )

                ProfileDetails(icon = R.drawable.ic_exit, title = "خروج", true) {
                    showBottomSheet = true
                }
            }
        }

        if (showBottomSheet) {
            CustomViews.BottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState,
                "خروج"
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                    text = "مطمئن هستید میخواهید از حساب کاربری خود خارج شوید؟",
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(modifier = Modifier.weight(0.4f), shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ), onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val response = service.logout()

                                if (response.isSuccessful) {
                                    withContext(Dispatchers.Main) {
                                        context.startActivity(
                                            Intent(
                                                context,
                                                LoginSignup::class.java
                                            )
                                        )

                                        (context as? ComponentActivity)?.finishAffinity()
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        ToastUtils.toast(context, "مشکلی پیش آمده، مجدد تلاش کنید")
                                    }
                                }
                            }
                        }) {
                        Text(
                            text = "اره",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(30.dp))

                    Button(modifier = Modifier.weight(0.2f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Color.Gray,
                        ), onClick = {
                            showBottomSheet = false
                        }) {
                        Text(
                            text = "نه",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetails(icon: Int, title: String, exit: Boolean = false, onClick: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .height(72.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier
                    .size(30.dp)
                    .weight(0.2f),
                painter = painterResource(id = icon),
                contentDescription = "account"
            )

            Text(
                modifier = Modifier.weight(if (!exit) 0.8f else 1f),
                text = title,
                color = if (!exit) Color.Black else Color.Red
            )


            if (!exit)
                Image(
                    modifier = Modifier
                        .weight(0.2f)
                        .size(15.dp),
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "arrow"
                )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun GreetingPreview2() {
    TaskTheme {
        Profile()
    }
}