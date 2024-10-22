package ir.abyx.task.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gmail.hamedvakhide.compose_jalali_datepicker.JalaliDatePickerDialog
import ir.abyx.task.R
import ir.abyx.task.data.remote.RetrofitService
import ir.abyx.task.data.remote.model.TaskModel
import ir.abyx.task.ui.customView.CustomViews
import ir.abyx.task.ui.ext.DialogType
import ir.abyx.task.ui.ext.FieldSection
import ir.abyx.task.ui.ext.TaskStatus
import ir.abyx.task.ui.ext.ToastUtils
import ir.abyx.task.ui.theme.TaskTheme
import ir.abyx.task.ui.utils.Utils
import ir.huri.jcal.JalaliCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class TasksActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val projectId = intent.getStringExtra("projectId")

        enableEdgeToEdge()
        setContent {
            TaskTheme {
                TaskScreen(projectId)
            }
        }
    }
}

suspend fun getTasks(context: Context, projectId: String): List<TaskModel> {
    return withContext(Dispatchers.IO) {
        val response = RetrofitService.taskService.getTasks(projectId)

        if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            withContext(Dispatchers.Main) {
                if (response.code() == 404)
                    ToastUtils.toast(context, "تسکی برای نمایش وجود ندارد")
                else
                    ToastUtils.toast(context, "مشکلی در بارگیری لیست تسک‌ها به‌وجود‌ آمده")
            }
            emptyList()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(projectId: String?) {
    val context = LocalContext.current

    var toDoItems by remember { mutableStateOf(listOf<TaskModel>()) }
    var doingItems by remember { mutableStateOf(listOf<TaskModel>()) }
    var doneItems by remember { mutableStateOf(listOf<TaskModel>()) }

    var showDialog by remember { mutableStateOf(false) }

    var dialogType by remember { mutableStateOf<DialogType?>(null) }

    val sections = listOf(
        "تسک ها" to toDoItems,
        "در حال انجام" to doingItems,
        "انجام شده" to doneItems
    )

    LaunchedEffect(Unit) {
        projectId?.let {
            val tasks = getTasks(context, it)

            toDoItems = tasks.filter { task -> task.status == TaskStatus.TODO }
            doingItems = tasks.filter { task -> task.status == TaskStatus.IN_PROGRESS }
            doneItems = tasks.filter { task -> task.status == TaskStatus.DONE }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.main))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(700.dp)
                    .align(Alignment.BottomCenter)
                    .background(colorResource(id = R.color.white_back))
                    .padding(16.dp),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    sections.forEachIndexed { index, (title, items) ->
                        if (index == 0) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = title,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clipToBounds()
                                            .clickable {
                                                dialogType = DialogType.AddTask
                                                showDialog = true
                                            },
                                        color = colorResource(id = R.color.main)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.ic_add),
                                            contentDescription = "Add Task"
                                        )
                                    }
                                }
                            }
                        } else {
                            item {
                                Text(
                                    text = title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }

                        items(items) { task ->
                            TaskItem(context, projectId!!, task = task)
                        }
                    }
                }
            }
        }

        if (showDialog) {
            when (dialogType) {
                DialogType.AddTask -> FullScreenDialog(
                    context,
                    projectId!!,
                    null,
                    onDismiss = { showDialog = false },
                    onListChange = {
                        toDoItems = it.filter { task -> task.status == TaskStatus.TODO }
                        doingItems = it.filter { task -> task.status == TaskStatus.IN_PROGRESS }
                        doneItems = it.filter { task -> task.status == TaskStatus.DONE }
                        showDialog = false
                    }
                )

                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullScreenDialog(
    context: Context?,
    id: String?,
    task: TaskModel?,
    onDismiss: () -> Unit,
    onListChange: (List<TaskModel>) -> Unit
) {
    var titleValue by remember { mutableStateOf(task?.title ?: "") }
    var descValue by remember { mutableStateOf(task?.desc ?: "") }
    var statusValue by remember { mutableStateOf(task?.status ?: TaskStatus.TODO) }
    var startDateValue by remember { mutableStateOf(task?.startDatetime ?: "") }
    var endDateValue by remember { mutableStateOf(task?.endDatetime ?: "") }
    var startTimeValue by remember {
        mutableStateOf(
            task?.startDatetime?.split("T")?.getOrNull(1) ?: ""
        )
    }
    var endTimeValue by remember {
        mutableStateOf(
            task?.endDatetime?.split("T")?.getOrNull(1) ?: ""
        )
    }
    var taskWeightValue by remember { mutableStateOf(task?.taskWeight ?: "0") }
    var progressValue by remember { mutableIntStateOf(task?.progress ?: 0) }
    var tempProgressValue by remember { mutableIntStateOf(progressValue) }
    var titleError by remember { mutableStateOf(false) }
    var startDateError by remember { mutableStateOf(false) }
    var endDateError by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var fieldState by remember {
        mutableStateOf(FieldSection.State)
    }

    val openCalendar = remember { mutableStateOf(false) }
    var openTimePicker by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white_back))
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { onDismiss() },
                        painter = painterResource(id = R.drawable.ic_cancel),
                        contentDescription = "cancel"
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                        ) {
                            Text(text = "نام پروژه", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "#1 ۱۶ شهریور، توسط شخص ۱",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }

                        Text(
                            modifier = Modifier
                                .clickable {
                                    if (titleValue.isEmpty()) {
                                        titleError = true
                                        return@clickable
                                    } else
                                        titleError = false

                                    if (startDateValue.isEmpty()) {
                                        startDateError = true
                                        return@clickable
                                    } else
                                        startDateError = false

                                    if (endDateValue.isEmpty()) {
                                        endDateError = true
                                        return@clickable
                                    } else
                                        endDateError = false

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val response = RetrofitService.taskService.createTask(
                                            id!!,
                                            TaskModel(
                                                title = titleValue,
                                                desc = descValue,
                                                status = statusValue,
                                                startDatetime = Utils.jalaliToGregorian(
                                                    startDateValue,
                                                    startTimeValue
                                                ),
                                                endDatetime = Utils.jalaliToGregorian(
                                                    endDateValue,
                                                    endTimeValue
                                                ),
                                                taskWeight = taskWeightValue,
                                                progress = progressValue,
                                                users = listOf(),
                                                tags = listOf(),
                                                projectId = id
                                            )
                                        )

                                        withContext(Dispatchers.Main) {
                                            if (response.isSuccessful) {
                                                ToastUtils.toast(
                                                    context!!,
                                                    "تسک با موفقیت ساخته شد"
                                                )
                                                onListChange(getTasks(context, id))
                                            } else {
                                                ToastUtils.toast(
                                                    context!!,
                                                    "مشکلی در ایجاد تسک به‌وجود آمده"
                                                )
                                            }
                                        }
                                    }
                                },
                            text = "ذخیره",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.light_blue)
                        )

                    }
                }

                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "اطلاعات",
                    style = MaterialTheme.typography.titleLarge,
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.White
                        )
                ) {
                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        text = "عنوان تسک",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )

                    CustomViews.CustomTextField(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        titleValue,
                        "عنوان تسک",
                        onValueChange = { titleValue = it })

                    if (titleError) {
                        Text(
                            text = "عنوان تسک نمی‌تواند خالی باشد",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorResource(id = R.color.white_back))
                            .height(1.dp)
                    )

                    Text(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        text = "توضیحات",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )

                    CustomViews.CustomTextField(
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        ),
                        descValue,
                        "توضیحات تسک را اینجا وارد کنید ...",
                        onValueChange = { descValue = it })
                }

                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "جزئیات",
                    style = MaterialTheme.typography.titleLarge,
                )

                DetailRows(
                    "وضعیت",
                    when (statusValue) {
                        TaskStatus.TODO -> "در انتظار"
                        TaskStatus.IN_PROGRESS -> "در حال انجام"
                        TaskStatus.DONE -> "انجام شده"
                    }.toString(),
                    R.drawable.ic_arrow_down,
                    onClick = {
                        showBottomSheet = !showBottomSheet
                        fieldState = FieldSection.State
                    }
                )

                DetailRows("تاریخ شروع",
                    if (startDateValue.isNotEmpty()) {
                        "$startDateValue, $startTimeValue"
                    } else {
                        ""
                    },
                    R.drawable.ic_arrow_down,
                    onClick = {
//                        showBottomSheet = !showBottomSheet
                        fieldState = FieldSection.StartDate
                        openCalendar.value = true
                    })

                if (startDateError) {
                    Text(
                        text = "تاریخ شروع تسک نمی‌تواند خالی باشد",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }

                DetailRows("تاریخ پایان",
                    if (endDateValue.isNotEmpty()) {
                        "$endDateValue, $endTimeValue"
                    } else {
                        ""
                    },
                    R.drawable.ic_arrow_down,
                    onClick = {
//                        showBottomSheet = !showBottomSheet
                        fieldState = FieldSection.EndDate
                        openCalendar.value = true
                    })

                if (endDateError) {
                    Text(
                        text = "تاریخ پایان تسک نمی‌تواند خالی باشد",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
                    )
                }

                DetailRows("زمان مورد نیاز", taskWeightValue, R.drawable.ic_arrow_down,
                    onClick = {
                        showBottomSheet = !showBottomSheet
                        fieldState = FieldSection.Weight
                    })

                DetailRows("درصد پیشرفت", "${progressValue}%", R.drawable.ic_arrow_down,
                    onClick = {
                        showBottomSheet = !showBottomSheet
                        fieldState = FieldSection.Progress
                    })

                if (openCalendar.value) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        JalaliDatePickerDialog(
                            openDialog = openCalendar,
                            initialDate = if (fieldState == FieldSection.StartDate) {
                                if (startDateValue.isEmpty()) {
                                    null
                                } else {

                                    val parts = startDateValue.split("T")
                                    val datePart = parts[0].trim()

                                    val dateParts = datePart.split("-")
                                    val year = dateParts[0].toInt()
                                    val month = dateParts[1].toInt()
                                    val day = dateParts[2].toInt()

                                    JalaliCalendar(year, month, day)
                                }
                            } else {
                                if (endDateValue.isEmpty()) {
                                    null
                                } else {

                                    val parts = endDateValue.split("T")
                                    val datePart = parts[0].trim()

                                    val dateParts = datePart.split("-")
                                    val year = dateParts[0].toInt()
                                    val month = dateParts[1].toInt()
                                    val day = dateParts[2].toInt()

                                    JalaliCalendar(year, month, day)
                                }
                            },
                            onSelectDay = {
                                Log.d("Date", "onSelect: ${it.day} ${it.month} ${it.year}")
                            },
                            onConfirm = {
                                if (fieldState == FieldSection.StartDate) {
                                    startDateValue = "${it.year}-${it.month}-${it.day}"
                                } else {
                                    endDateValue = "${it.year}-${it.month}-${it.day}"
                                }
                                openTimePicker = true
                            },
//                                backgroundColor = colorResource(id = R.color.white_back),
//                                textColor = Color.Gray,
////                                selectedIconColor = colorResource(id = R.color.light_blue),
//                                nextPreviousBtnColor = Color.Black,
//                                dropDownColor = Color.Black,
//                                dayOfWeekLabelColor = Color.Black
                        )
                    }
                }

                if (openTimePicker) {
                    CustomViews.DialWithDialog(
                        onConfirm = { selectedHour, selectedMinute ->
                            openTimePicker = false
                            val selectedTime =
                                String.format("%02d:%02d", selectedHour, selectedMinute)
                            if (fieldState == FieldSection.StartDate)
                                startTimeValue = selectedTime
                            else
                                endTimeValue = selectedTime
                        },
                        onDismiss = {
                            openTimePicker = false
                        }
                    )
                }

                if (showBottomSheet) {
                    CustomViews.BottomSheet(
                        onDismissRequest = {
                            if (taskWeightValue.isEmpty())
                                taskWeightValue = "0"

                            tempProgressValue = progressValue

                            showBottomSheet = false
                        },
                        sheetState,

                        when (fieldState) {
                            FieldSection.State -> "وضعیت"
                            FieldSection.StartDate -> "تاریخ شروع"
                            FieldSection.EndDate -> "تاریخ پایان"
                            FieldSection.Weight -> "زمان مورد نیاز"
                            FieldSection.Progress -> "درصد پیشرفت"
                        }

                    ) {
                        when (fieldState) {
                            FieldSection.State -> {
                                val statuses = listOf(
                                    TaskStatus.TODO to "در انتظار",
                                    TaskStatus.IN_PROGRESS to "در حال انجام",
                                    TaskStatus.DONE to "انجام شده"
                                )

                                statuses.forEach { (status, label) ->
                                    CustomViews.StatusRow(
                                        text = label,
                                        isSelected = (statusValue == status),
                                        onClick = {
                                            statusValue = status
                                            showBottomSheet = false
                                        }
                                    )
                                }
                            }

                            FieldSection.StartDate -> {}

                            FieldSection.EndDate -> {}

                            FieldSection.Weight -> {
                                CustomViews.CustomTextField(
                                    modifier = Modifier,
                                    value = taskWeightValue,
                                    keyboardOptions = KeyboardType.Number,
                                    onValueChange = {
                                        if (it.all { char -> char.isDigit() }) {
                                            taskWeightValue = it
                                        }
                                    },
                                    label = "ساعت"
                                )

                                Spacer(modifier = Modifier.height(22.dp))

                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        colorResource(id = R.color.main)
                                    ),
                                    contentPadding = PaddingValues(),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = {
                                        if (taskWeightValue.isEmpty())
                                            taskWeightValue = "0"
                                        showBottomSheet = false
                                    }
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .wrapContentWidth(Alignment.CenterHorizontally),
                                        text = "ذخیره",
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                }
                            }

                            FieldSection.Progress -> {

                                CustomViews.LineSliderImpl(
                                    progress = tempProgressValue,
                                    onSliderValueChange = { newTempValue ->
                                        tempProgressValue = newTempValue
                                    }
                                )

                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        colorResource(id = R.color.main)
                                    ),
                                    contentPadding = PaddingValues(),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = {
                                        progressValue = tempProgressValue
                                        showBottomSheet = false
                                    }
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                            .wrapContentWidth(Alignment.CenterHorizontally),
                                        text = "ذخیره",
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "مسئولین",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .clipToBounds()
                            .clickable { },
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit"
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(
                            16.dp
                        )
                        .fillMaxWidth()
                ) {
                    CustomViews.CustomView("سپهر آزادی", R.drawable.ic_launcher_foreground)
                }

                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "تگ‌ها",
                        style = MaterialTheme.typography.titleLarge,
                    )

                    Image(
                        modifier = Modifier
                            .size(30.dp)
                            .clipToBounds()
                            .clickable { },
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit"
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(
                            16.dp
                        )
                        .fillMaxWidth()
                ) {
                    CustomViews.CustomView("تست", 0)
                }
            }
        }
    }
}

@Composable
fun DetailRows(title: String, value: String, img: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White
            )
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )

        Text(
            modifier = Modifier.weight(1.5f),
            text = value
        )

        Image(
            painter = painterResource(id = img),
            contentDescription = "Image"
        )
    }

    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    )
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskItem(context: Context, id: String, task: TaskModel) {
    var showDialog by remember { mutableStateOf(false) }
    var timeDifference by remember { mutableStateOf("") }
    var timeFraction by remember { mutableFloatStateOf(1f) }

    val (startDatePart, startTime) = Utils.splitDateTime(task.startDatetime)
    val (startYear, startMonth, startDay) = Utils.splitDate(startDatePart)

    val (endDatePart, endTime) = Utils.splitDateTime(task.endDatetime)
    val (endYear, endMonth, endDay) = Utils.splitDate(endDatePart)

    //need to clean up
    val startDateTime = LocalDateTime.of(
        startYear, startMonth, startDay,
        startTime.split(":")[0].toInt(), startTime.split(":")[1].toInt()
    )

    LaunchedEffect(Unit) {
        while (true) {
            val (difference, fraction) = Utils.calculateTimeDifference(
                endYear,
                endMonth,
                endDay,
                endTime,
                startDateTime
            )

            // Update the state values
            timeDifference = difference
            timeFraction = fraction

            // Delay 1 minute before the next update
            delay(60000L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clipToBounds()
            .clickable {
                showDialog = true
            }
    ) {

        if (showDialog) {
            FullScreenDialog(
                context = context,
                id = id,
                task = task,
                onDismiss = { showDialog = false },
                onListChange = {}
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .offset(y = (-7).dp)
                .background(Color.LightGray)
                .clipToBounds()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = timeFraction)
                    .fillMaxHeight()
                    .background(color = colorResource(id = R.color.line))
            )
        }

        Column(
            modifier = Modifier
                .padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp
                )
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    text = task.title,
                )

                Text(
                    text = timeDifference,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = "${Utils.gregorianToJalali(startDatePart).day} ${
                    Utils.getMonthStr(
                        Utils.gregorianToJalali(
                            startDatePart
                        ).month
                    )
                } | $startTime",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Surface(shape = CircleShape) {
                        task.users.forEach { _ ->
                            Image(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(Color.Blue),
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "placeholder"
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CustomViews.CircularProgressBarWithText(task.progress)

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = task.taskWeight,
                        color = Color.Black,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false, device = Devices.PIXEL_7)
@Composable
fun TaskPreview() {
    FullScreenDialog(null, null, null, onDismiss = {}) {

    }
}