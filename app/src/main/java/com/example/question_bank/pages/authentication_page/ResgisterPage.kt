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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.question_bank.data.User
import com.example.question_bank.pages.utils.CustomTextField
import com.example.question_bank.repositories.authentication.FirebaseAuth
import com.example.question_bank.repositories.fireStore.FireStoreClient
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(email: String, navController : NavHostController) {

    val userName = remember { mutableStateOf("") }
    val emailId = remember { mutableStateOf(email) }
    val organization = remember { mutableStateOf("") }
    val passWord = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val showPassword = remember { mutableStateOf(false)}
    val confirmShowPassword = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val auth = FirebaseAuth()
    val fireStoreClient = FireStoreClient()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(MainColor),
                title = { Text("Register", color = TextColor) }
            )
        },
        containerColor = MainColor
    ) { it ->
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //User Name field
            CustomTextField(
                value = userName.value,
                onValueChange = {userName.value = it},
                label = "Name",
                placeHolder = "Enter Your Name",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Name",
                        tint = IconColor
                    )
                },
                isPassWord = false
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Email field
            CustomTextField(
                value = emailId.value,
                onValueChange = {emailId.value = it},
                label = "Email",
                placeHolder = "Enter Your Email",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Email,
                        contentDescription = "Email Id",
                        tint = IconColor
                    )
                },
                isPassWord = false
            )
            Spacer(modifier = Modifier.height(20.dp))
            
            CustomTextField(
                value = organization.value,
                onValueChange = {
                    organization.value = it
                },
                label = "Organization",
                placeHolder = "Enter Your Organization",
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Oraganization",
                        tint = IconColor
                    )
                },
                isPassWord = false
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            //PassWord field
            CustomTextField(
                value = passWord.value,
                onValueChange = {passWord.value = it},
                label = "PassWord",
                placeHolder = "Enter Your PassWord",
                trailingIcon = {
                    IconButton(onClick = {showPassword.value = !showPassword.value}) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "PassWord",
                            tint = IconColor
                        )
                    }
                },
                isPassWord = !showPassword.value
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Confirm password field
            CustomTextField(
                value = confirmPassword.value,
                onValueChange = {confirmPassword.value = it},
                label = "Confirm PassWord",
                placeHolder = "Enter Your PassWord",
                trailingIcon = {
                    IconButton(onClick = {confirmShowPassword.value = !confirmShowPassword.value}) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Confirm PassWord",
                            tint = IconColor
                        )
                    }
                },
                isPassWord = !confirmShowPassword.value
            )

            Spacer(modifier = Modifier.height(20.dp))

            //Register button

            ElevatedButton(
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.elevatedButtonColors(MainColor),
                onClick = {
                    if(userName.value.isNotEmpty() && passWord.value.isNotEmpty() && confirmPassword.value.isNotEmpty() ) {
                        if(passWord.value == confirmPassword.value) {
                            scope.launch {
                                val user = auth. register(userName.value, emailId.value, passWord.value)
                                if(user.user != null) {
                                    val userId = user.user?.uid.toString()
                                    try{
                                        fireStoreClient.insert(
                                            User(
                                                id = userId,
                                                name = userName.value,
                                                email = emailId.value,
                                                organization = organization.value,
                                            )
                                        ).collect { docId ->
                                            if (docId != null) {
                                                navController.navigate("home")
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Please wait while we register you",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }catch(e : FirebaseAuthUserCollisionException) {
                                        auth.logout()
                                        Toast.makeText(context, "Account already in use", Toast.LENGTH_SHORT).show()
                                        println("Error: " +e.message)
                                    }

                                }else {
                                    Toast.makeText(context, "Please try again", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }else {
                            Toast.makeText(context, "PassWord and Confirm Password should be same", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text("Register Account", color = TextColor)
            }

            Row(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account ?")
                TextButton(onClick = {
                    navController.navigate("login?email=${emailId.value}")
                }) {
                    Text("Click Here" , color = TextColor, style = TextStyle(
                        fontWeight = FontWeight.Bold
                    ))
                }
            }
        }

    }

}