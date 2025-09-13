package com.example.question_bank.repositories.viewModels.loginViewModel.dataViewModel

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.question_bank.repositories.FireBaseProvider
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri

data class Organization(
    val id: String = "",
    val Name: String = "",
)

data class Course(
    val id: String = "",
    val Name: String = "",
)

data class CourseFile(
    val id: String = "",
    val Name: String = "",
    val url: String = "",
)

data class CourseData(
    val course: Course,
    val files: List<CourseFile> = emptyList<CourseFile>(),
)

class OrganizationViewModel : ViewModel() {

    val fireStore = FireBaseProvider.fireStore
    val storage = FireBaseProvider.storage

    private val _organizations = MutableStateFlow<List<Organization>>(emptyList())
    val organization: StateFlow<List<Organization>> = _organizations

    private val _organizationData = MutableStateFlow<Organization?>(null)
    val organizationData: StateFlow<Organization?> = _organizationData

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses

    private val _courseData = MutableStateFlow<CourseData?>(null)
    val courseData: StateFlow<CourseData?> = _courseData

    init {
        loadAllOrganization()
    }

    suspend fun addCourse(orgId: String, courseName: String): Boolean {
        return try {
            val newCourse = hashMapOf(
                "Name" to courseName,
                "files" to emptyList<Map<String, Any>>() // empty files collection
            )

            fireStore.collection("organizations")
                .document(orgId)
                .collection("courses")
                .add(newCourse)
                .await() // await the result
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun loadAllOrganization() {

        viewModelScope.launch {
            try {
                val snapShot = fireStore.collection("organizations").get().await()

                val orgList = snapShot.documents.mapNotNull { doc ->
                    doc.toObject(Organization::class.java)?.copy(id = doc.id)
                }

                _organizations.value = orgList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadAllCourses(orgId: String) {
        try {
            viewModelScope.launch {
                val snapShot = fireStore.collection("organizations")
                    .document(orgId)
                    .collection("courses")
                    .get()
                    .await()

                val courseList = snapShot.documents.mapNotNull {
                    it.toObject(Course::class.java)?.copy(id = it.id)

                }
                _courses.value = courseList
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun loadCourseInformation(orgId: String, courseId: String) {

        viewModelScope.launch {
            try {

                val courseDoc = fireStore.collection("organizations")
                    .document(orgId)
                    .collection("courses")
                    .document(courseId)
                    .get()
                    .await()

                val course = courseDoc.toObject(Course::class.java)?.copy(id = courseDoc.id)

                // Get all files under that course
                val filesSnapshot = fireStore.collection("organizations")
                    .document(orgId)
                    .collection("courses")
                    .document(courseId)
                    .collection("files")
                    .get()
                    .await()

                val files = filesSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(CourseFile::class.java)?.copy(id = doc.id)
                }

                if (course != null) {
                    _courseData.value = CourseData(course = course, files = files)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun getOrganizationInformation(orgId: String) {

        viewModelScope.launch {
            try {

                val snapshot = fireStore.collection("organizations").document(orgId).get().await()

                val orgInfo = snapshot.toObject<Organization>()?.copy(id = snapshot.id)
                _organizationData.value = orgInfo

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadFile(context: Context, fileUrl: String, fileName: String) {
        try {
            val uri = fileUrl.toUri()
            val request = DownloadManager.Request(
                uri
            )
                .setTitle(fileName)
                .setVisibleInDownloadsUi(true)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setMimeType(getMimeType(uri.toString()))
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
            Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Download Failed. Please try again...", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun getMimeType(url: String): String {
        val extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(url)
        return android.webkit.MimeTypeMap.getSingleton()
            .getMimeTypeFromExtension(extension.lowercase()) ?: "*/*"
    }


    fun getStorageFolderType(fileName: String): String {
        return when {
            fileName.endsWith(".pdf", ignoreCase = true) -> "pdf"
            fileName.endsWith(".jpg", ignoreCase = true) ||
                    fileName.endsWith(".jpeg", ignoreCase = true) ||
                    fileName.endsWith(".png", ignoreCase = true) -> "image"
            else -> "others"
        }
    }


    suspend fun uploadFileAndGetUrl(fileName: String, fileUri: Uri): String? {
        return try {
            val folderName = getStorageFolderType(fileName)
            val storageReference = storage.reference.child("$folderName/$fileName")

            storageReference.putFile(fileUri).await()
            storageReference.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    fun saveUrlToFireStore(orgId: String, courseId: String, fileName: String, fileUrl: String) {

        val fileData = hashMapOf(
            "Name" to fileName,
            "url" to fileUrl
        )
        viewModelScope.launch {
            fireStore.collection("organizations")
                .document(orgId)
                .collection("courses")
                .document(courseId)
                .collection("files")
                .add(fileData)
                .await()
        }
    }

    fun clearOrganizationInformation() {
        _organizationData.value = null
    }
}