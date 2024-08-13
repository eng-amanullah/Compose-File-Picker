package com.amanullah.filepicker.customview

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.amanullah.filepicker.R
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Preview
@Composable
fun ChooseFromGalleryAndCamera(
    modifier: Modifier = Modifier,
    galleryButtonCallBack: (File) -> Unit = {},
    cameraButtonCallBack: (File) -> Unit = {},
    skipButtonCallBak: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Camera Uri state
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let {
                val file = uriToFile(context, it)
                file?.let { file ->
                    galleryButtonCallBack(file)
                }
            }
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            file?.let { file ->
                cameraButtonCallBack(file)
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(size = 16.dp),
        border = BorderStroke(width = 1.dp, color = Color(0xFFE8E8E8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .padding(end = 16.dp),
                    text = stringResource(R.string.choose_from),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = {
                        skipButtonCallBak()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.skip),
                        color = Color(0xFF4E4E4E),
                        style = TextStyle(
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                // Photo Gallery
                Card(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(size = 16.dp),
                    border = BorderStroke(width = (0.5).dp, color = Color(0xFFD5ECE2)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9F5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    filePickerLauncher.launch("*/*")
                                }
                            }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(color = Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(16.dp),
                                painter = painterResource(id = R.drawable.ic_select_from_gallery_icon),
                                contentDescription = null
                            )
                        }

                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            text = stringResource(R.string.photo_gallery),
                            color = Color.Black,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Open Camera
                Card(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(size = 16.dp),
                    border = BorderStroke(width = (0.5).dp, color = Color(0xFFD5ECE2)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F9F5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                scope.launch {
                                    val uri = createImageUri(context)
                                    cameraUri = uri
                                    cameraLauncher.launch(uri)
                                }
                            }
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(color = Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(16.dp),
                                painter = painterResource(id = R.drawable.ic_select_from_camera_icon),
                                contentDescription = null
                            )
                        }

                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            text = stringResource(R.string.open_camera),
                            color = Color.Black,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

// Helper function to create a Uri for the camera image
private fun createImageUri(context: Context): Uri {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileProvider",
        file
    )
}

private fun uriToFile(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, getFileName(context, uri))

    return try {
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

private fun getFileName(context: Context, uri: Uri): String {
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    val name = cursor?.getColumnIndex("_display_name")?.let { columnIndex ->
        cursor.moveToFirst()
        cursor.getString(columnIndex)
    }
    cursor?.close()
    return name ?: "unknown_file"
}