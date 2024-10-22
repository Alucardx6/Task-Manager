package ir.abyx.task.ui.customView

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.core.graphics.ColorUtils
import ir.abyx.task.R
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.roundToInt

object CustomViews {

    @Composable
    fun CustomTextField(
        modifier: Modifier,
        value: String,
        onValueChange: (String) -> Unit,
        label: String = "",
        isSingleLine: Boolean = true,
        isError: Boolean = false,
        errorMessage: String = "",
        icon: Boolean = false,
        onIconClick: (() -> Unit)? = null,
        focusedTextColor: Color = Color.Black,
        unfocusedTextColor: Color = Color.Black,
        keyboardOptions: KeyboardType = KeyboardType.Text,
        focusedBorderColor: Color = colorResource(id = R.color.main),
        unfocusedBorderColor: Color = Color.LightGray,
        focusedLabelColor: Color = colorResource(id = R.color.main),
        unfocusedLabelColor: Color = Color.LightGray,
        errorColor: Color = Color.Red,
        enabled: Boolean = true
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(modifier = modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(text = label, fontSize = 18.sp) },
                    isError = isError,
                    textStyle = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = isSingleLine,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardOptions),
                    colors = OutlinedTextFieldDefaults.colors(
                        errorTextColor = focusedTextColor,
                        focusedTextColor = focusedTextColor,
                        unfocusedTextColor = unfocusedTextColor,
                        focusedBorderColor = focusedBorderColor,
                        unfocusedBorderColor = unfocusedBorderColor,
                        focusedLabelColor = focusedLabelColor,
                        unfocusedLabelColor = unfocusedLabelColor,
                        errorBorderColor = errorColor,
                        errorLabelColor = errorColor
                    ),
                    enabled = enabled,
                    trailingIcon = if (icon) {
                        {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable { onIconClick?.invoke() },
                                painter = painterResource(id = R.drawable.ic_check_circle),
                                contentDescription = "icon",
                                tint = if (isError) errorColor else colorResource(id = R.color.light_blue)
                            )
                        }
                    } else null
                )

                if (isError) {
                    Text(
                        text = errorMessage,
                        color = errorColor,
                        style = TextStyle(fontSize = 12.sp),
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun BottomSheet(
        onDismissRequest: () -> Unit,
        sheetState: SheetState,
        headerTitle: String,
        content: @Composable () -> Unit
    ) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header with a dismiss button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp, start = 16.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = headerTitle, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    Text(
                        modifier = Modifier.clickable { onDismissRequest() },
                        text = "بستن",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }

                content()
            }
        }
    }

    @Composable
    fun StatusRow(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = if (isSelected) {
                    colorResource(id = R.color.light_blue)
                } else {
                    Color.Black
                }
            )

            if (isSelected) {
                Image(
                    modifier = Modifier.padding(end = 16.dp),
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Check"
                )
            }
        }
    }


    @Composable
    fun UniversalDialog(
        modifier: Modifier = Modifier,
        title: String,
        onDismissRequest: () -> Unit,
        onConfirm: () -> Unit,
        content: @Composable () -> Unit
    ) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = title,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    content()

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        TextButton(onClick = onConfirm) {
                            Text("تایید", fontSize = 16.sp, color = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(onClick = onDismissRequest) {
                            Text("بیخیال!", fontSize = 16.sp, color = Color.Black)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CustomView(text: String, img: Int) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.LightGray
        ) {
            Row(modifier = Modifier.padding(2.dp), verticalAlignment = Alignment.CenterVertically) {
                if (img != 0) {
                    Surface(shape = CircleShape) {
                        Image(
                            modifier = Modifier
                                .size(30.dp)
                                .background(Color.Blue),
                            painter = painterResource(id = img),
                            contentDescription = "placeholder"
                        )
                    }
                }
                Text(modifier = Modifier.padding(4.dp), text = text)
            }
        }
    }

    @Composable
    fun CustomTextField(
        modifier: Modifier,
        titleValue: String,
        hint: String,
        onValueChange: (String) -> Unit,
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            // Hint text
            if (titleValue.isEmpty()) {
                Text(
                    text = hint,
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }

            // BasicTextField
            BasicTextField(
                value = titleValue,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp)
            )
        }
    }

    @Composable
    fun LineSliderImpl(
        modifier: Modifier = Modifier,
        progress: Int,
        onSliderValueChange: (Int) -> Unit
    ) {
        var tempValue by remember { mutableIntStateOf(progress) }

        LineSlider(
            value = tempValue,
            onValueChange = {
                tempValue = it
                onSliderValueChange(it)
            },
            modifier = modifier
                .padding(horizontal = 16.dp)
                .widthIn(max = 400.dp),
            steps = 100,
            thumbDisplay = { it.toString() }
        )
    }

    @SuppressLint("Range")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LineSlider(
        value: Int,
        onValueChange: (Int) -> Unit,
        modifier: Modifier = Modifier,
        steps: Int = 0,
        valueRange: IntRange = 0..100,
        thumbDisplay: (Int) -> String = { "" },
    ) {

        val thumbSize = 64.dp

        val interaction = remember { MutableInteractionSource() }
        val isDragging by interaction.collectIsDraggedAsState()
        val density = LocalDensity.current
        val offsetHeight by animateFloatAsState(
            targetValue = with(density) { if (isDragging) 36.dp.toPx() else 0.dp.toPx() },
            animationSpec = spring(
                stiffness = Spring.StiffnessMediumLow,
                dampingRatio = Spring.DampingRatioLowBouncy
            ), label = "offsetAnimation"
        )

        val animatedValue by animateFloatAsState(
            targetValue = value.toFloat(),
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy
            ), label = "animatedValue"
        )

        Slider(
            value = animatedValue,
            onValueChange = { onValueChange(it.roundToInt()) },
            modifier = modifier,
            valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
            steps = steps - 1,
            interactionSource = interaction,
            thumb = {},
            track = { sliderState ->

                val fraction by remember {
                    derivedStateOf {
                        (animatedValue - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                    }
                }

                var width by remember { mutableIntStateOf(0) }

                Box(
                    Modifier
                        .clearAndSetSemantics { }
                        .height(thumbSize)
                        .fillMaxWidth()
                        .onSizeChanged { width = it.width },
                ) {
                    Box(
                        Modifier
                            .zIndex(10f)
                            .align(Alignment.CenterStart)
                            .offset {
                                IntOffset(
                                    x = lerp(
                                        start = -(thumbSize / 2).toPx(),
                                        end = width - (thumbSize / 2).toPx(),
                                        t = fraction
                                    ).roundToInt(),
                                    y = -offsetHeight.roundToInt(),
                                )
                            }
                            .size(thumbSize)
                            .padding(10.dp)
                            .shadow(
                                elevation = 10.dp,
                                shape = CircleShape,
                            )
                            .background(
                                color = colorResource(id = R.color.main),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            thumbDisplay(value),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }

                    val strokeColor = MaterialTheme.colorScheme.onSurface
                    val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
                    Box(
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .drawWithCache {
                                onDrawBehind {
                                    scale(
                                        scaleY = 1f,
                                        scaleX = if (isLtr) 1f else -1f
                                    ) {
                                        drawSliderPath(
                                            fraction = fraction,
                                            offsetHeight = offsetHeight,
                                            color = strokeColor,
                                            steps = sliderState.steps
                                        )
                                    }
                                }
                            }
                    )
                }
            }
        )
    }


    private fun DrawScope.drawSliderPath(
        fraction: Float,
        offsetHeight: Float,
        color: Color,
        steps: Int,
    ) {

        val path = Path()
        val activeWidth = size.width * fraction
        val midPointHeight = size.height / 2
        val curveHeight = midPointHeight - offsetHeight
        val beyondBounds = size.width * 2
        val ramp = 72.dp.toPx()


        // Point far beyond the right edge
        path.moveTo(
            x = beyondBounds,
            y = midPointHeight
        )

        // Line to the "base" right before the curve
        path.lineTo(
            x = activeWidth + ramp,
            y = midPointHeight
        )

        // Smooth curve to the top of the curve
        path.cubicTo(
            x1 = activeWidth + (ramp / 2),
            y1 = midPointHeight,
            x2 = activeWidth + (ramp / 2),
            y2 = curveHeight,
            x3 = activeWidth,
            y3 = curveHeight,
        )

        // Smooth curve down the curve to the "base" on the other side
        path.cubicTo(
            x1 = activeWidth - (ramp / 2),
            y1 = curveHeight,
            x2 = activeWidth - (ramp / 2),
            y2 = midPointHeight,
            x3 = activeWidth - ramp,
            y3 = midPointHeight
        )

        // Line to a point far beyond the left edge
        path.lineTo(
            x = -beyondBounds,
            y = midPointHeight
        )

        val variation = .1f

        // Line to a point far beyond the left edge
        path.lineTo(
            x = -beyondBounds,
            y = midPointHeight + variation
        )

        // Line to the "base" right before the curve
        path.lineTo(
            x = activeWidth - ramp,
            y = midPointHeight + variation
        )

        // Smooth curve to the top of the curve
        path.cubicTo(
            x1 = activeWidth - (ramp / 2),
            y1 = midPointHeight + variation,
            x2 = activeWidth - (ramp / 2),
            y2 = curveHeight + variation,
            x3 = activeWidth,
            y3 = curveHeight + variation,
        )

        // Smooth curve down the curve to the "base" on the other side
        path.cubicTo(
            x1 = activeWidth + (ramp / 2),
            y1 = curveHeight + variation,
            x2 = activeWidth + (ramp / 2),
            y2 = midPointHeight + variation,
            x3 = activeWidth + ramp,
            y3 = midPointHeight + variation,
        )

        // Line to a point far beyond the right edge
        path.lineTo(
            x = beyondBounds,
            y = midPointHeight + variation
        )

        val exclude = Path().apply {
            addRect(Rect(-beyondBounds, -beyondBounds, 0f, beyondBounds))
            addRect(Rect(size.width, -beyondBounds, beyondBounds, beyondBounds))
        }

        val trimmedPath = Path()
        trimmedPath.op(path, exclude, PathOperation.Difference)

        val pathMeasure = PathMeasure()
        pathMeasure.setPath(trimmedPath, false)

        val graduations = steps + 1
        for (i in 0..graduations) {
            val pos = pathMeasure.getPosition((i / graduations.toFloat()) * pathMeasure.length / 2)
            val height = 10f
            when (i) {
                0, graduations -> drawCircle(
                    color = color,
                    radius = 10f,
                    center = pos
                )

                else -> drawLine(
                    strokeWidth = if (pos.x < activeWidth) 4f else 2f,
                    color = color,
                    start = pos + Offset(0f, height),
                    end = pos + Offset(0f, -height),
                )
            }
        }

        clipRect(
            left = -beyondBounds,
            top = -beyondBounds,
            bottom = beyondBounds,
            right = activeWidth,
        ) {
            drawTrimmedPath(trimmedPath, color)
        }
        clipRect(
            left = activeWidth,
            top = -beyondBounds,
            bottom = beyondBounds,
            right = beyondBounds,
        ) {
            drawTrimmedPath(trimmedPath, color.copy(alpha = .2f))
        }

    }

    private fun DrawScope.drawTrimmedPath(path: Path, color: Color) {
        drawPath(
            path = path,
            color = color,
            style = Stroke(
                width = 10f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round,
            ),
        )
    }

    private fun lerp(start: Float, end: Float, t: Float) = start + t * (end - start)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DialWithDialog(
        onConfirm: (Int, Int) -> Unit,
        onDismiss: () -> Unit,
    ) {
        val currentTime = Calendar.getInstance()

        val timePickerState = rememberTimePickerState(
            initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
            initialMinute = currentTime.get(Calendar.MINUTE),
            is24Hour = false,
        )


        TimePickerDialog(
            onDismiss = { onDismiss() },
            onConfirm = {
                // Pass the selected hour and minute to the caller
                onConfirm(timePickerState.hour, timePickerState.minute)
            }
        ) {
            TimePicker(
                colors = TimePickerDefaults.colors(
                    clockDialColor = colorResource(id = R.color.white_back),
                    selectorColor = colorResource(id = R.color.light_blue),
                    clockDialUnselectedContentColor = Color.Gray,
                    clockDialSelectedContentColor = colorResource(id = R.color.white_back),
                    timeSelectorSelectedContentColor = colorResource(id = R.color.light_blue),
                    timeSelectorUnselectedContainerColor = colorResource(id = R.color.white_back),
                    timeSelectorSelectedContainerColor = colorResource(id = R.color.white_back),
                    timeSelectorUnselectedContentColor = Color.Gray,
                    periodSelectorBorderColor = Color.Transparent,
                    periodSelectorSelectedContentColor = Color.White,
                    periodSelectorSelectedContainerColor = colorResource(id = R.color.light_blue),
                    periodSelectorUnselectedContentColor = Color.Gray,
                    periodSelectorUnselectedContainerColor = colorResource(id = R.color.white_back)
                ),
                state = timePickerState,
            )
        }
    }

    @Composable
    fun TimePickerDialog(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit,
        content: @Composable () -> Unit
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            AlertDialog(
                onDismissRequest = onDismiss,

                confirmButton = {
                    TextButton(onClick = { onConfirm() }) {
                        Text("تایید", color = colorResource(id = R.color.light_blue))
                    }
                },

                dismissButton = {
                    TextButton(onClick = { onDismiss() }) {
                        Text("کنسل", color = Color.Gray)
                    }
                },
                text = { content() },
                containerColor = Color.White,
            )
        }
    }

    @Composable
    fun CircularProgressBarWithText(progress: Int) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.size(25.dp),
                color = Color.Cyan,
                trackColor = Color.Gray,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$progress%",
                color = Color.DarkGray
            )
        }
    }

    @Composable
    fun UserProfileImage(name: String, modifier: Modifier = Modifier, rounded: Boolean = false) {
        val initials = getInitials(name)

        val backgroundColor = generateColorFromText(initials)

        Box(
            modifier = if (rounded) {
                modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
            } else {
                modifier
                    .size(80.dp)
                    .background(backgroundColor)
            }

        ) {
            Text(
                text = initials,
                fontSize = if (rounded) 22.sp else 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        val firstInitial = parts.getOrNull(0)?.firstOrNull()?.toString() ?: ""
        val lastInitial = parts.getOrNull(1)?.firstOrNull()?.toString() ?: ""
        return "$firstInitial\u200c$lastInitial"
    }

    private fun generateColorFromText(text: String): Color {
        val hash = text.hashCode()
        val hue = abs(hash) % 360
        val color = ColorUtils.HSLToColor(floatArrayOf(hue.toFloat(), 0.5f, 0.7f))
        return Color(color)
    }
}