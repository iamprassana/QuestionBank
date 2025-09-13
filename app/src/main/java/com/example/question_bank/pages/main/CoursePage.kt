package com.example.question_bank.pages.main

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.question_bank.pages.utils.FilePicker
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.CourseFile
import com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel.OrganizationViewModel
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor
import com.example.question_bank.ui.theme.TextColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursePage(
    navController: NavController,
    courseId: String,
    orgId: String,
    ovm: OrganizationViewModel,
) {

    LaunchedEffect(Unit) {
        ovm.loadCourseInformation(orgId, courseId)
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val data by ovm.courseData.collectAsState()

    // Launcher to pick a file
    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val fileName = com.example.question_bank.pages.utils.getFileName(context, uri)
            scope.launch {
                val url = ovm.uploadFileAndGetUrl(fileUri = uri, fileName = fileName)
                if (url != null) {
                    ovm.saveUrlToFireStore(
                        orgId = orgId, courseId = courseId, fileName = fileName, fileUrl = url
                    )
                    Toast.makeText(context, "File Uploaded", Toast.LENGTH_SHORT).show()
                    ovm.loadCourseInformation(orgId, courseId)
                }
            }
        } else {
            Toast.makeText(context, "No File Selected", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "${data?.course?.Name} Questions" ?: " ",
                        style = TextStyle(color = TextColor, fontSize = 20.sp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(MainColor)
            )
        },
        containerColor = MainColor,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { pickFileLauncher.launch("*/*") },
                containerColor = MainColor
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add File", tint = IconColor)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            data?.let { courseData ->
                items(courseData.files) { files ->
                    FileDecoration(files) {
                        ovm.downloadFile(context, files.url, files.Name)
                    }
                }
            }
        }
    }
}

@Composable
fun FileDecoration(files: CourseFile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MainColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = files.Name,
                color = TextColor,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onClick() }) {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Download File",
                    tint = IconColor
                )
            }
        }
    }
}