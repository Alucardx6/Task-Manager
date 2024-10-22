package ir.abyx.task.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import coil.compose.rememberAsyncImagePainter
import ir.abyx.task.R
import ir.abyx.task.data.remote.RetrofitService
import ir.abyx.task.data.remote.ext.MyApplication
import ir.abyx.task.data.remote.model.ProjectModel
import ir.abyx.task.data.remote.model.UserModel
import ir.abyx.task.ui.customView.CustomViews
import ir.abyx.task.ui.ext.ReminderWorker
import ir.abyx.task.ui.ext.ToastUtils
import ir.abyx.task.ui.theme.TaskTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskTheme {
                MainScreen()
            }
        }
    }
}

suspend fun getUser(context: Context): UserModel? {
    return withContext(Dispatchers.IO) {
        val response = RetrofitService.userService.getUser()

        if (response.isSuccessful) {
            val user = response.body()
            if (user != null) {
                return@withContext user
            } else {
                ToastUtils.toast(context, "اطلاعات کاربری شما نامعتبر است")
                return@withContext null
            }
        } else {
            ToastUtils.toast(context, "مشکلی در بارگیری اطلاعات شما به‌وجود آمده")
            return@withContext null
        }
    }
}

suspend fun getProjects(context: Context): List<ProjectModel> {
    return withContext(Dispatchers.IO) {
        val response = RetrofitService.projectService.getProjects()

        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            withContext(Dispatchers.Main) {
                ToastUtils.toast(context, "مشکلی در بارگیری لیست پروژه‌ها به‌وجود‌ آمده")
            }
            emptyList()
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    var projectsList by remember {
        mutableStateOf(
            listOf<ProjectModel>()
        )
    }
    var user by remember { mutableStateOf<UserModel?>(null) }

    var showDialog by remember { mutableStateOf(false) }
    var currentProject by remember { mutableStateOf<ProjectModel?>(null) }


    LaunchedEffect(Unit) {
        projectsList = getProjects(context)
        user = getUser(context)
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.main))
        ) {

            Column(modifier = Modifier.fillMaxWidth()) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    if (user?.profilePicture.isNullOrEmpty()) {
                        CustomViews.UserProfileImage(
                            name = user?.name ?: "کاربر",
                            modifier = Modifier
                                .clickable {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            ProfileActivity::class.java
                                        )
                                    )
                                }
                                .size(50.dp),
                            true
                        )
                    } else {
                        // Show the profile picture if it's available
                        Surface(modifier = Modifier.clip(CircleShape)) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = MyApplication.baseUrl + user?.profilePicture,
                                    error = painterResource(R.drawable.ic_launcher_background), // Fallback if the image fails to load
                                ),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .clickable {
                                        context.startActivity(
                                            Intent(
                                                context,
                                                ProfileActivity::class.java
                                            )
                                        )
                                    }
                                    .size(50.dp)
                            )
                        }
                    }

                }

                Text(
                    modifier = Modifier
                        .padding(start = 16.dp),
                    text = "پروژه ها",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            val radioOptions = listOf("در حال انجام", "به اتمام رسیده")
            val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp, start = 24.dp)
                ) {
                    radioOptions.forEach { text ->
                        Column(
                            modifier = Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = {
                                        onOptionSelected(text)
                                    },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                )
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = text,
                                fontSize = 20.sp,
                                color = if (text == selectedOption) Color.White else Color.Gray,
                                modifier = Modifier
                                    .padding(bottom = 4.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .height(1.dp)
                                    .background(
                                        if (text == selectedOption) Color.White else Color.Transparent
                                    )
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(650.dp),
                    color = colorResource(id = R.color.white_back),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                ) {
                    Column {

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                        ) {
                            items(if (selectedOption == radioOptions[0]) {
                                projectsList.filter { !it.state }
                            } else {
                                projectsList.filter { it.state }
                            }) { item ->
                                CustomListItem(
                                    item = item,
                                    context,
                                    onEdit = {
                                        currentProject = item
                                        showDialog = true
                                    },
                                    onDelete = {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            val response =
                                                RetrofitService.projectService.deleteProject(item.id!!)

                                            if (response.isSuccessful) {
                                                withContext(Dispatchers.Main) {

                                                    projectsList =
                                                        projectsList.toMutableList().also { list ->
                                                            list.removeIf { it.id == item.id }
                                                        }
                                                    ToastUtils.toast(
                                                        context,
                                                        "پروژه با موفقیت حذف شد"
                                                    )
                                                }
                                            } else {
                                                withContext(Dispatchers.Main) {
                                                    ToastUtils.toast(
                                                        context,
                                                        "مشکلی در حذف پروژه به‌وجود‌ آمده"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                context,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                onListChange = { newItem ->
                    projectsList = newItem
                    showDialog = false
                }
            )
        }

        if (showDialog) {
            CustomViews.UniversalDialog(
                title = if (currentProject != null) "ویرایش پروژه" else "ایجاد پروژه جدید",
                onDismissRequest = {
                    showDialog = false
                    currentProject = null
                },
                onConfirm = {
                    if (currentProject != null) {
                        editProject(
                            context,
                            itemsList = projectsList,
                            currentItem = currentProject!!,
                            updatedTitle = currentProject!!.title
                        ) { updatedList ->
                            projectsList = updatedList
                            currentProject = null
                            showDialog = false
                        }
                    }
                },
                content = {
                    ProjectContent(
                        titleText = currentProject?.title ?: "",
                        onTitleChange = { newTitle ->
                            currentProject =
                                currentProject?.copy(title = newTitle)
                        },
                        titleError = false
                    )
                }
            )
        }
    }
}

fun editProject(
    context: Context,
    itemsList: List<ProjectModel>,
    currentItem: ProjectModel,
    updatedTitle: String,
    onProjectUpdated: (List<ProjectModel>) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        val updatedProject = currentItem.copy(title = updatedTitle)

        val response = RetrofitService.projectService.editProject(
            currentItem.id!!,
            ProjectModel(title = updatedProject.title)
        )

        if (response.isSuccessful) {
            withContext(Dispatchers.Main) {
                val updatedList = itemsList.map {
                    if (it.id == currentItem.id) {
                        updatedProject
                    } else {
                        it
                    }
                }
                onProjectUpdated(updatedList)
                ToastUtils.toast(context, "پروژه با موفقیت ویرایش شد")
            }
        } else {

            withContext(Dispatchers.Main) {
                ToastUtils.toast(context, "مشکلی در ویرایش پروژه به‌وجود آمده")
            }
        }
    }
}

@Composable
fun FloatingActionButton(
    context: Context,
    modifier: Modifier = Modifier,
    onListChange: (List<ProjectModel>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var titleText by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    val service = RetrofitService.projectService

    FloatingActionButton(
        onClick = {
            showDialog = true
        },
        modifier = modifier
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Bottom))
            .padding(16.dp),
        shape = CircleShape,
        containerColor = colorResource(id = R.color.main)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "add"
        )
    }

    if (showDialog) {
        CustomViews.UniversalDialog(
            title = "ایجاد پروژه جدید",
            onDismissRequest = {
                showDialog = false
                titleText = ""
            },
            onConfirm = {
                val isTitleEmpty = titleText.isEmpty()

                titleError = isTitleEmpty

                if (!isTitleEmpty) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = service.createProjects(ProjectModel(title = titleText))

                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                ToastUtils.toast(context, "پروژه با موفقیت ساخته شد")

                                onListChange(getProjects(context))

                                showDialog = false
                                titleText = ""
                            } else {
                                ToastUtils.toast(context, "مشکلی در ایجاد پروژه به‌وجود آمده")
                            }
                        }
                    }
                }
            }
        ) {
            ProjectContent(
                titleText = titleText,
                onTitleChange = { titleText = it },
                titleError = titleError
            )
        }
    }
}

@Composable
fun ProjectContent(
    titleText: String,
    onTitleChange: (String) -> Unit,
    titleError: Boolean
) {
    Column {
        CustomViews.CustomTextField(
            Modifier,
            value = titleText,
            onValueChange = onTitleChange,
            label = "عنوان",
            isError = titleError,
            errorMessage = "عنوان نمی‌تواند خالی باشد"
        )
    }
}


fun scheduleReminder(context: Context, title: String, description: String, delayInMinutes: Long) {
    val reminderData = workDataOf("title" to title, "message" to description)

    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(false)
        .setRequiresCharging(false)
        .build()

    val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
        .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
        .setConstraints(constraints)
        .setInputData(reminderData)
        .build()

    WorkManager.getInstance(context).enqueue(reminderRequest)
    Toast.makeText(context, "Reminder set for: $delayInMinutes Minutes", Toast.LENGTH_LONG).show()
}

@Composable
fun CustomListItem(
    item: ProjectModel,
    context: Context,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .clip(shape = RoundedCornerShape(8.dp))
                .background(Color.White)
                .clickable {
                    val intent = Intent(context, TasksActivity::class.java).apply {
                        putExtra("projectId", item.id)
                    }
                    context.startActivity(intent)
                }
                .padding(top = 8.dp, bottom = 8.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    modifier = Modifier
                        .fillMaxHeight(), checked = false, onCheckedChange = {})

                VerticalDivider(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .width(1.dp)
                        .height(100.dp),
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(1.4f),
                            text = item.createdAt ?: "",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )

                        Image(
                            modifier = Modifier
                                .weight(0.2f)
                                .height(30.dp)
                                .clipToBounds()
                                .clip(shape = RoundedCornerShape(8.dp))
                                .alpha(if (item.state) 0.5f else 1f)
                                .clickable(enabled = !item.state) { onEdit() },
                            contentScale = ContentScale.FillBounds,
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "edit"
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        Image(
                            modifier = Modifier
                                .weight(0.2f)
                                .height(30.dp)
                                .clipToBounds()
                                .clip(shape = RoundedCornerShape(8.dp))
                                .alpha(if (item.state) 0.5f else 1f)
                                .clickable(enabled = !item.state) { onDelete() },
                            contentScale = ContentScale.FillBounds,
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "delete"
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 8.dp),
                        text = item.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun GreetingPreview() {
    TaskTheme {
        MainScreen()
    }
}