package com.example.question_bank.pages.utils

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.question_bank.ui.theme.IconColor
import com.example.question_bank.ui.theme.MainColor

fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                result = it.getString(it.getColumnIndexOrThrow(android.provider.OpenableColumns.DISPLAY_NAME))
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            result = result?.substring(cut + 1)
        }
    }
    return result ?: "file"
}



@Composable
fun FilePicker(context: Context, onFileSelected: (Uri, String) -> Unit) {

    val selectedUri = remember { mutableStateOf<Uri?>(null) }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            Toast.makeText(context, "Choose File", Toast.LENGTH_SHORT).show()
        } else {
            val fileName = getFileName(context, uri)
            selectedUri.value = uri
            Toast.makeText(context, "File Selected ", Toast.LENGTH_SHORT).show()
            //Upload to firebase action
            onFileSelected(uri, fileName)
        }
    }

    FloatingActionButton(
        onClick = {
            pickFileLauncher.launch("*/*")
        },
        containerColor = MainColor
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Pick File",
            tint = IconColor
        )
    }

//    if (uri == null) {
//        Text("No File Selected")
//        return
//
//    }

//    val mimeType = context.contentResolver.getType(uri) ?: " "
//
//    if (mimeType.startsWith("image/") || mimeType.startsWith("application/pdf")) {
//        AsyncImage(
//            model = uri,
//            contentDescription = "Selected Image",
//            modifier = Modifier
//                .wrapContentSize()
//                .padding(20.dp)
//        )
//    } else {
//        Text("Something went wrong. Please try again.")
//    }

}