package ir.abyx.task.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.abyx.task.R
import ir.abyx.task.data.remote.RetrofitService
import ir.abyx.task.data.remote.model.PasswordModel
import ir.abyx.task.data.remote.model.UserModel
import ir.abyx.task.ui.customView.CustomViews
import ir.abyx.task.ui.ext.EmailNotificationState
import ir.abyx.task.ui.ext.NotificationsState
import ir.abyx.task.ui.ext.SettingType
import ir.abyx.task.ui.ext.ToastUtils
import ir.abyx.task.ui.ui.theme.TaskTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileSettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val name = intent.getStringExtra("name")
            val ordinal = intent.getIntExtra("type", 0)
            val type = SettingType.entries[ordinal]

            TaskTheme {
                ProfileSetting(
                    name ?: "",
                    type = type
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetting(intentName: String, type: SettingType?) {

    val service = RetrofitService.userService

    var currentName by remember { mutableStateOf(intentName) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember {
        mutableStateOf("")
    }

    var notificationState by remember {
        mutableStateOf(NotificationsState.Email)
    }

    var bottomSheetTitle by remember {
        mutableStateOf("تغییر رمزعبور")
    }

    var name by remember { mutableStateOf(currentName) }

    var emailNotification by remember { mutableStateOf(EmailNotificationState.Instantly) }

    val context = LocalContext.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = if (type == SettingType.Profile) "اطلاعات اکانت" else "اعلانات",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(20.dp))

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                (context as? ComponentActivity)?.finish()
                            },
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back"
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                if (type == SettingType.Profile)
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = "اطلاعات شخصی",
                        fontSize = 18.sp
                    )

                if (type == SettingType.Profile) {

                    CustomViews.CustomTextField(
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp),
                        label = "نام",
                        value = name,
                        onValueChange = { newValue ->
                            name = newValue
                        },
                        icon = name != currentName,
                        onIconClick = {
                            CoroutineScope(Dispatchers.IO).launch {

//                                val response = service.updateUser(
//                                    UserModel(
//                                        name = name
//                                    ).toFieldMap()
//                                )

                                val response = service.updateUser(UserModel(name = name))

                                withContext(Dispatchers.Main) {
                                    if (response.isSuccessful) {
                                        ToastUtils.toast(
                                            context,
                                            "نام کاربری شما با موفقیت ویرایش شد"
                                        )
                                        currentName = name
                                    } else {
                                        ToastUtils.toast(
                                            context,
                                            "مشکلی پیش آمده، مجدد تلاش کنید"
                                        )
                                    }
                                }
                            }
                        }
                    )

                    Spacer(
                        modifier = Modifier
                            .padding(top = 24.dp, bottom = 24.dp)
                            .height(1.dp)
                            .background(Color.LightGray)
                            .fillMaxWidth()
                    )

                    ProfileDetails(icon = R.drawable.ic_camera, title = "تغییر رمزعبور") {
                        bottomSheetTitle = "تغییر رمزعبور"
                        showBottomSheet = true
                    }
                } else {
                    ProfileDetails(icon = R.drawable.ic_email, title = "اعلانات ایمیل") {
                        bottomSheetTitle = "اعلانات ایمیل"
                        notificationState = NotificationsState.Email
                        showBottomSheet = true
                    }

                    ProfileDetails(icon = R.drawable.ic_notifications, title = "یادآور تسک") {
                        bottomSheetTitle = "یادآور تسک"
                        notificationState = NotificationsState.TaskReminder
                        showBottomSheet = true
                    }
                }

            }
        }

        if (showBottomSheet) {
            CustomViews.BottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState,
                bottomSheetTitle
            ) {
                if (type == SettingType.Profile) {
                    CustomViews.CustomTextField(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                        label = "رمزعبور فعلی",
                        value = currentPassword,
                        onValueChange = { currentPassword = it }
                    )

                    CustomViews.CustomTextField(
                        modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        label = "رمزعبور جدید",
                        value = newPassword,
                        onValueChange = { newPassword = it }
                    )

                    Button(modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color.Green
                        ),
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val response =
                                    service.updatePassword(
                                        PasswordModel(
                                            newPassword = newPassword,
                                            oldPassword = currentPassword
                                        )
                                    )

                                withContext(Dispatchers.Main) {
                                    if (response.isSuccessful) {
                                        ToastUtils.toast(context, "رمزعبور شما با موفقیت تغییر کرد")
                                        showBottomSheet = false
                                    } else {
                                        if (response.code() == 401) {
                                            ToastUtils.toast(
                                                context,
                                                "رمزعبور فعلی شما صحیح نمی‌باشد"
                                            )
                                        } else {
                                            ToastUtils.toast(
                                                context,
                                                "مشکلی پیش آمده، مجدد تلاش کنید"
                                            )
                                        }
                                    }
                                }
                            }
                        }) {

                        Text(
                            modifier = Modifier.padding(4.dp),
                            text = "تایید",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    when (notificationState) {
                        NotificationsState.Email -> {
                            val statuses = listOf(
                                EmailNotificationState.Disable to "غیرفعال",
                                EmailNotificationState.Instantly to "بلافاصله",
                                EmailNotificationState.EveryHour to "هر ۱ ساعت",
                                EmailNotificationState.Every3Hour to "هر ۳ ساعت",
                                EmailNotificationState.Daily to "روزانه",
                            )

                            statuses.forEach { (status, label) ->
                                CustomViews.StatusRow(
                                    text = label,
                                    isSelected = (emailNotification == status),
                                    onClick = {
                                        emailNotification = status
                                        showBottomSheet = false
                                    }
                                )
                            }
                        }

                        NotificationsState.TaskReminder -> {

                        }
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun GreetingPreview3() {
    TaskTheme {
        ProfileSetting("", null)
    }
}