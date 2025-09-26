package com.example.question_bank.pages.authentication_page

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.question_bank.pages.utils.CustomTextField
import com.example.question_bank.repositories.authentication.FirebaseAuth
import com.example.question_bank.repositories.viewModels.loginViewModel.LoginViewModel
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    email: String,
    navController: NavHostController,
    loginViewModel: LoginViewModel = viewModel(),
) {
    val emailState by loginViewModel.email.collectAsState()
    val passWordState by loginViewModel.password.collectAsState()
    val showPassword by loginViewModel.showPassword.collectAsState()
    val isLoading by loginViewModel.isLoading.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    val auth = FirebaseAuth()

    // Show toast when error occurs
    errorMessage?.let { msg ->
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        loginViewModel.resetError()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Login") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MainColor,
                    titleContentColor = TextColor
                ),
            )
        },
        modifier = Modifier.fillMaxSize(),
        containerColor = MainColor
    ) { it ->

        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            CustomTextField(
                value = emailState,
                onValueChange = { loginViewModel.setUserName(it) },
                label = "Email",
                placeHolder = "Enter Your Email",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email",
                        tint = IconColor
                    )
                },
                isPassWord = false
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomTextField(
                value = passWordState,
                onValueChange = { loginViewModel.setPassword(it) },
                label = "Password",
                placeHolder = "Enter Your Password",
                trailingIcon = {
                    IconButton(onClick = { loginViewModel.toggleShowPassword() }) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Show Password",
                            tint = IconColor,
                        )
                    }
                },
                isPassWord = !showPassword
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        //Call firebase for sending reset password email.
                        loginViewModel.resetPassWord(auth, emailState, { success, error ->
                            if (success) {
                                Toast.makeText(
                                    context,
                                    "Password reset email sent!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                            }
                            loginViewModel.resetError()
                        })
                    },
                    colors = ButtonDefaults.buttonColors(MainColor)
                ) {
                    Text("Forgot Password")
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            if (!isLoading) {
                ElevatedButton(
                    onClick = {
                        loginViewModel.login(auth) {
                            navController.popBackStack()
                            navController.navigate("home")
                        }
                    },
                    colors = ButtonDefaults.elevatedButtonColors(MainColor),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .width(90.dp)
                ) {
                    Text("Login", color = TextColor)
                }
            } else {
                CircularProgressIndicator()
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Don't have an account ?")
                TextButton(onClick = {
                    navController.navigate("register?email=$emailState")
                }) {
                    Text(
                        "Register Here", color = TextColor, style = TextStyle(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}



