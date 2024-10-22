package ir.abyx.task.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.abyx.task.R
import ir.abyx.task.data.remote.RetrofitService
import ir.abyx.task.data.remote.ext.ErrorUtils
import ir.abyx.task.data.remote.model.EmailRequest
import ir.abyx.task.data.remote.model.LoginRequest
import ir.abyx.task.data.remote.model.RegisterRequest
import ir.abyx.task.data.remote.model.ResetPassword
import ir.abyx.task.data.remote.model.VerifyRequest
import ir.abyx.task.ui.customView.CustomViews
import ir.abyx.task.ui.ext.LoginState
import ir.abyx.task.ui.ext.ToastUtils
import ir.abyx.task.ui.theme.TaskTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
class LoginSignup : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaskTheme {
                Login()
            }
        }
    }
}

val service = RetrofitService.authService

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Login() {

    var titleText by remember {
        mutableStateOf("ورود")
    }
    var loginOrSignupText by remember {
        mutableStateOf("ایجاد حساب")
    }
    var state by remember { mutableStateOf(LoginState.Login) }
    var pervState by remember { mutableStateOf(LoginState.Login) }

    var usernameText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var reEnterPasswordText by remember { mutableStateOf("") }
    var verifyCodeText by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var reEnterPasswordError by remember { mutableStateOf(false) }
    var verifyCodeError by remember { mutableStateOf(false) }

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    val exceptionHandler = ErrorUtils.createCoroutineExceptionHandler(
        scope = scope,
        snackBarHostState = snackBarHostState,
        onLoadingFinished = { isLoading = false }
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackBarHostState) { snackBarData ->
                    Snackbar(
                        snackbarData = snackBarData,
                        containerColor = Color.Red,
                        contentColor = Color.White
                    )
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.main))
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 16.dp),
                ) {
                    Spacer(modifier = Modifier.height(100.dp))
                    Image(
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.CenterHorizontally),
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "Logo"
                    )

                    Text(
                        modifier = Modifier
                            .padding(top = 32.dp, start = 24.dp),
                        text = titleText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (state == LoginState.Signup)
                        CustomViews.CustomTextField(
                            Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
                            value = usernameText,
                            onValueChange = { usernameText = it },
                            label = "نام کاربری",
                            isError = usernameError,
                            errorMessage = "نام کاربری نمی‌تواند خالی باشد",
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = colorResource(id = R.color.line),
                            focusedLabelColor = colorResource(id = R.color.line),
                            enabled = !isLoading
                        )

                    if (state != LoginState.Verify && state != LoginState.ResetPassword)
                        CustomViews.CustomTextField(
                            Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
                            value = emailText,
                            onValueChange = { emailText = it },
                            label = "ایمیل",
                            isError = emailError,
                            errorMessage = "ایمیل نمی‌تواند خالی باشد",
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = colorResource(id = R.color.line),
                            focusedLabelColor = colorResource(id = R.color.line),
                            enabled = !isLoading
                        )

                    if (state == LoginState.Verify || state == LoginState.ResetPassword)
                        CustomViews.CustomTextField(
                            Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
                            value = verifyCodeText,
                            onValueChange = {
                                if (it.all { char -> char.isDigit() }) {
                                    verifyCodeText = it
                                }
                            },
                            label = "کد تایید",
                            keyboardOptions = KeyboardType.Number,
                            isError = verifyCodeError,
                            errorMessage = "کد تایید نمی‌تواند خالی باشد",
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = colorResource(id = R.color.line),
                            focusedLabelColor = colorResource(id = R.color.line),
                            enabled = !isLoading
                        )

                    if (state != LoginState.Verify && state != LoginState.ForgetPassword)
                        CustomViews.CustomTextField(
                            Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
                            value = passwordText,
                            onValueChange = { passwordText = it },
                            label = "رمزعبور",
                            isError = passwordError,
                            errorMessage = "رمزعبور نمی‌تواند خالی باشد",
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = colorResource(id = R.color.line),
                            focusedLabelColor = colorResource(id = R.color.line),
                            enabled = !isLoading
                        )

                    if (state == LoginState.Signup)
                        CustomViews.CustomTextField(
                            Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp),
                            value = reEnterPasswordText,
                            onValueChange = { reEnterPasswordText = it },
                            label = "تکرار رمز عبور",
                            isError = reEnterPasswordError,
                            errorMessage = "تکرار رمزعبور نمی‌تواند خالی باشد",
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = colorResource(id = R.color.line),
                            focusedLabelColor = colorResource(id = R.color.line),
                            enabled = !isLoading
                        )

                    Button(modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (isLoading) 0.5f else 1f)
                        .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            colorResource(id = R.color.line)
                        ),
                        onClick = {
                            if (!isLoading) {
                                focusManager.clearFocus()

                                val isEmailEmpty = emailText.isEmpty()
                                val isPasswordEmpty = passwordText.isEmpty()

                                emailError = isEmailEmpty
                                passwordError = isPasswordEmpty

                                isLoading = true

                                when (state) {
                                    LoginState.Login -> {
                                        if (!isEmailEmpty && !isPasswordEmpty) {
                                            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                                                val response = service.loginUser(
                                                    LoginRequest(
                                                        email = emailText,
                                                        password = passwordText
                                                    )
                                                )

                                                if (response.isSuccessful) {
                                                    withContext(Dispatchers.Main) {
                                                        context.startActivity(
                                                            Intent(
                                                                context,
                                                                MainActivity::class.java
                                                            )
                                                        )
                                                        (context as? ComponentActivity)?.finish()
                                                    }

                                                } else {
                                                    if (response.code() == 403) {
                                                        state = LoginState.Verify
                                                        pervState = LoginState.Login
                                                        titleText = "تایید ایمیل"
                                                        loginOrSignupText = "بازگشت"
                                                    } else {
                                                        withContext(Dispatchers.Main) {
                                                            scope.launch {
                                                                snackBarHostState.showSnackbar(
                                                                    message = "اطلاعات وارد شده صحیح نمی‌باشد",
                                                                    duration = SnackbarDuration.Short
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                                isLoading = false
                                            }
                                        } else {
                                            isLoading = false
                                        }
                                    }

                                    LoginState.Signup -> {
                                        val isUsernameEmpty = usernameText.isEmpty()
                                        val isReEnterPasswordEmpty = passwordText.isEmpty()
                                        usernameError = isUsernameEmpty
                                        reEnterPasswordError = isReEnterPasswordEmpty

                                        if (!isEmailEmpty && !isPasswordEmpty && !isReEnterPasswordEmpty && !isUsernameEmpty) {
                                            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                                                val response = service.registerUser(
                                                    RegisterRequest(
                                                        name = usernameText,
                                                        email = emailText,
                                                        password = passwordText
                                                    )
                                                )

                                                if (response.isSuccessful) {
                                                    state = LoginState.Verify
                                                    pervState = LoginState.Signup
                                                    titleText = "تایید ایمیل"
                                                    loginOrSignupText = "بازگشت"
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        scope.launch {
                                                            snackBarHostState.showSnackbar(
                                                                message = response.message(),
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                }
                                                isLoading = false
                                            }
                                        } else {
                                            isLoading = false
                                        }
                                    }

                                    LoginState.Verify -> {
                                        val isVerifyEmpty = verifyCodeText.isEmpty()
                                        verifyCodeError = isVerifyEmpty
                                        if (!isVerifyEmpty) {

                                            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                                                val response = service.verifyUser(
                                                    VerifyRequest(
                                                        email = emailText,
                                                        code = verifyCodeText
                                                    )
                                                )

                                                if (response.isSuccessful) {
                                                    titleText = "ورود"
                                                    loginOrSignupText = "ایجاد حساب"
                                                    usernameText = ""
                                                    emailText = ""
                                                    passwordText = ""
                                                    reEnterPasswordText = ""
                                                    state = LoginState.Login

                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        scope.launch {
                                                            snackBarHostState.showSnackbar(
                                                                message = response.message(),
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                }
                                                isLoading = false
                                            }
                                        } else {
                                            isLoading = false
                                        }
                                    }

                                    LoginState.ForgetPassword -> {
                                        if (!isEmailEmpty) {
                                            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                                                val response =
                                                    service.forgotPassword(EmailRequest(emailText))

                                                if (response.isSuccessful) {
                                                    state = LoginState.ResetPassword
                                                    passwordError = false
                                                } else {
                                                    withContext(Dispatchers.Main) {
                                                        scope.launch {
                                                            snackBarHostState.showSnackbar(
                                                                message = response.message(),
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                }
                                                isLoading = false
                                            }
                                        } else {
                                            isLoading = false
                                        }
                                    }

                                    LoginState.ResetPassword -> {
                                        val isVerifyEmpty = verifyCodeText.isEmpty()
                                        verifyCodeError = isVerifyEmpty
                                        if (!isPasswordEmpty && !isVerifyEmpty) {
                                            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                                                val response =
                                                    service.resetPassword(
                                                        ResetPassword(
                                                            emailText,
                                                            verifyCodeText,
                                                            passwordText
                                                        )
                                                    )
                                                withContext(Dispatchers.Main) {
                                                    if (response.isSuccessful) {
                                                        state = LoginState.Login
                                                        ToastUtils.toast(
                                                            context,
                                                            "رمزعبور با موفقیت تغییر کرد"
                                                        )
                                                    } else {
                                                        scope.launch {
                                                            snackBarHostState.showSnackbar(
                                                                message = if (response.code() == 400) "کد وارد شده اشتباه است" else "کاربر مورد نظر یافت نشد",
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                    isLoading = false
                                                }
                                            }
                                        } else {
                                            isLoading = false
                                        }
                                    }
                                }
                            }
                        }) {
                        if (isLoading) {
                            CircularProgressIndicator(color = colorResource(id = R.color.light_blue))
                        } else {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp),
                                color = colorResource(id = R.color.white),
                                text = titleText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state == LoginState.Login || state == LoginState.Verify)
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 24.dp)
                                .alpha(if (isLoading) 0.5f else 1f)
                                .clickable {
                                    if (!isLoading) {
                                        if (state == LoginState.Login) {
                                            state = LoginState.ForgetPassword
                                            titleText = "بازیابی رمز عبور"
                                            loginOrSignupText = "بازگشت"
                                        } else {
                                            CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
                                                val response =
                                                    service.resendVerification(
                                                        EmailRequest(
                                                            emailText
                                                        )
                                                    )

                                                withContext(Dispatchers.Main) {
                                                    if (response.isSuccessful)
                                                        ToastUtils.toast(
                                                            context,
                                                            "کد تایید برای شما ارسال شد"
                                                        )
                                                    else {
                                                        scope.launch {
                                                            snackBarHostState.showSnackbar(
                                                                message = response.message(),
                                                                duration = SnackbarDuration.Short
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                            text = if (state == LoginState.Login) "فراموشی رمز عبور" else "ارسال مجدد",
                            fontSize = 18.sp,
                            color = if (state == LoginState.Login) Color.White else colorResource(id = R.color.light_blue)
                        )
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 24.dp)
                            .alpha(if (isLoading) 0.5f else 1f)
                            .clickable {
                                if (!isLoading) {
                                    when (state) {
                                        LoginState.Login -> {
                                            titleText = "ثبت نام"
                                            loginOrSignupText = "ورود"
                                            state = LoginState.Signup
                                        }

                                        LoginState.Signup -> {
                                            titleText = "ورود"
                                            loginOrSignupText = "ایجاد حساب"
                                            state = LoginState.Login
                                        }

                                        LoginState.Verify -> {
                                            if (pervState == LoginState.Login) {
                                                titleText = "ورود"
                                                loginOrSignupText = "ایجاد حساب"
                                                state = LoginState.Login
                                            } else {
                                                titleText = "ثبت نام"
                                                loginOrSignupText = "ورود"
                                                verifyCodeText = ""
                                                state = LoginState.Signup
                                            }
                                        }

                                        LoginState.ForgetPassword -> {
                                            titleText = "ورود"
                                            loginOrSignupText = "ایجاد حساب"
                                            state = LoginState.Login
                                        }

                                        LoginState.ResetPassword -> {
                                            state = LoginState.ForgetPassword
                                            titleText = "بازیابی رمز عبور"
                                            loginOrSignupText = "بازگشت"
                                        }
                                    }

                                    usernameError = false
                                    emailError = false
                                    passwordError = false
                                    reEnterPasswordError = false
                                }
                            },
                        text = loginOrSignupText,
                        fontSize = 18.sp,
                        color = Color.White,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    TaskTheme {
        Login()
    }
}