package com.romainperrier.tp1.user

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import com.romainperrier.tp1.data.API
import com.romainperrier.tp1.data.UserUpdate
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@OptIn(ExperimentalCoilApi::class)
class UserActivity : ComponentActivity() {

    private var bitmap: Bitmap? by mutableStateOf(null)
    private var uri: Uri? by mutableStateOf(null)

    private val viewModel: UserViewModel by lazy {
        ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UserActivityUI(viewModel)
        }
    }

    private fun Uri.toRequestBody(): MultipartBody.Part {
        val fileInputStream = contentResolver.openInputStream(this)!!
        val fileBody = fileInputStream.readBytes().toRequestBody()
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = fileBody
        )
    }

    private fun Bitmap.toRequestBody(): MultipartBody.Part {
        val tmpFile = File.createTempFile("avatar", "jpg")
        tmpFile.outputStream().use { // *use* se charge de faire open et close
            this.compress(Bitmap.CompressFormat.JPEG, 100, it) // *this* est le bitmap ici
        }
        return MultipartBody.Part.createFormData(
            name = "avatar",
            filename = "avatar.jpg",
            body = tmpFile.readBytes().toRequestBody()
        )
    }

    // propriété: une URI dans le dossier partagé "Images"
    private val captureUri by lazy {
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ContentValues())
    }

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun UserActivityUI(viewModel: UserViewModel) {


// launcher
        val takePicture =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                Log.d("UserActivity", "takePicture: $success")
                if (success) {
                    uri = captureUri
                    //send to API
                    lifecycleScope.launch {
                        uri?.let { API.userWebService.updateAvatar(it.toRequestBody()) }
                    }
                }
            }

        val takePictureLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                // Handle the captured Bitmap, if needed.
                this.bitmap = bitmap
                //send to API
                lifecycleScope.launch {
                    if (bitmap != null) {
                        API.userWebService.updateAvatar(bitmap.toRequestBody())
                    }
                }
            }
        val pickPictureLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback is invoked after the user selects a media item or closes the
                // photo picker.
                if (uri != null) {
                    this.uri = uri
                    //send to API
                    lifecycleScope.launch {
                        API.userWebService.updateAvatar(uri.toRequestBody())
                    }
                }
            }


        val requestPermissionLauncher =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    pickPictureLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    // show toast or something
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
//        var bitmap: Bitmap? by remember { mutableStateOf(null) }
//        var uri: Uri? by remember { mutableStateOf(null) }
        Column {
            AsyncImage(
                modifier = Modifier.fillMaxHeight(.2f),
                model = bitmap ?: uri,
                contentDescription = null
            )

            Button(
                onClick = {
                    takePicture.launch(captureUri)
                },
                content = { Text("Take picture") }
            )
            Button(
                onClick = {
                    if (android.os.Build.VERSION.SDK_INT <= 9) {
                        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                            pickPictureLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        } else {
                            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                    } else {
                        pickPictureLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    //pickPictureLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                content = { Text("Pick photo") }
            )
            EditUserInfoSection()
        }
    }


    @Composable
    fun EditUserInfoSection() {
        // Vous pouvez ajouter des champs de texte ou d'autres éléments pour éditer les informations de l'utilisateur
        // Vous pouvez utiliser viewModel pour accéder aux propriétés du viewModel

        val user by viewModel.user.collectAsState()

        TextField(
            value = user.name,
            onValueChange = { /* TODO: Mettez à jour le prénom dans le ViewModel */ },
            label = { Text("First Name") }
        )
        TextField(
            value = user.email,
            onValueChange = { /* TODO: Mettez à jour l'email dans le ViewModel */ },
            label = { Text("Email") }
        )

        Button(
            onClick = {
                // TODO: Appeler la méthode pour mettre à jour les informations de l'utilisateur dans le ViewModel
              viewModel.updateUserInfo(UserUpdate(user.name, user.email))
            },
            content = { Text("Update User Info") }
        )
    }

    private fun createImageCaptureUri(): Uri {
        // Logic to create a file URI for image capture.
        val file = File(externalMediaDirs.first(), "captured_image.jpg")
        return FileProvider.getUriForFile(this, "com.votre.package.fileprovider", file)
    }
}
