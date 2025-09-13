package com.example.question_bank.pages.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.Course
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.OrganizationViewModel
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationPage(
    navController: NavController,
    orgId: String,
    organizationVm: OrganizationViewModel
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        organizationVm.getOrganizationInformation(orgId)
        organizationVm.loadAllCourses(orgId = orgId)
    }

    val orgInfo by organizationVm.organizationData.collectAsState()
    val courseData by organizationVm.courses.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(MainColor),
                title = { Text("${ orgInfo?.Name} Courses" ?: "", style = TextStyle(color = TextColor, fontSize = 26.sp)) }
            )
        },
        containerColor = MainColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MainColor
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add a course",
                    tint = IconColor
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MainColor)
                .padding(paddingValues)
        ) {
            items(courseData) { course ->
                CourseDecoration(course = course) {
                    navController.navigate("course/${orgId}/${course.id}")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Show Dialog when FAB clicked
        if (showDialog) {
            CreateCourseDialog(
                onDismiss = { showDialog = false },
                onCreate = { courseName ->
                    scope.launch {
                        val success = organizationVm.addCourse(
                            orgId = orgId,
                            courseName = courseName
                        )
                        if (success) {
                            Toast.makeText(context, "Course Created", Toast.LENGTH_SHORT).show()
                            organizationVm.loadAllCourses(orgId)
                        } else {
                            Toast.makeText(context, "Failed to create course", Toast.LENGTH_SHORT).show()
                        }
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun CourseDecoration(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MainColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = course.Name, color = TextColor, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun CreateCourseDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var courseName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(MainColor),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Create New Course", color = TextColor, fontSize = 18.sp)

                TextField(
                    value = courseName,
                    onValueChange = {it ->
                        courseName =  it
                                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MainColor, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = { onDismiss() }, colors = ButtonDefaults.elevatedButtonColors(MainColor)) {
                        Text("Cancel", color = TextColor)
                    }
                    Button(
                        onClick = {
                            if (courseName.isNotBlank()) {
                                onCreate(courseName)
                            }
                        },
                        colors = ButtonDefaults.elevatedButtonColors(MainColor)
                    ) {
                        Text("Create", color = TextColor)
                    }
                }
            }
        }
    }
}


