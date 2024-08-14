package com.amanullah.filepicker.customview

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Keep
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amanullah.filepicker.R
import com.amanullah.filepicker.utils.Constants
import com.amanullah.filepicker.utils.createImageUri
import com.amanullah.filepicker.utils.uriToFile
import kotlinx.coroutines.launch
import java.io.File

@Preview
@Composable
fun ChooseFromGalleryAndCamera(
    modifier: Modifier = Modifier,
    buttonCallBack: (File) -> Unit = {},
    skipButtonCallBak: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val file = it.uriToFile(context = context)
            file?.let { file ->
                buttonCallBack(file)
            }
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = it.uriToFile(context = context)
            file?.let { file ->
                buttonCallBack(file)
            }
        }
    }

    // Camera Uri state
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri?.let {
                val file = it.uriToFile(context = context)
                file?.let { file ->
                    buttonCallBack(file)
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(size = 16.dp),
        border = BorderStroke(width = 1.dp, color = Color(color = 0xFFE8E8E8))
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
                        color = Color(color = 0xFF4E4E4E),
                        style = TextStyle(
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            val dataList = mutableListOf(
                ItemModel(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .padding(end = 8.dp),
                    icon = painterResource(id = R.drawable.ic_select_from_gallery_icon),
                    title = stringResource(R.string.photo_gallery),
                    type = Constants.GALLERY,
                    onClickCallBack = {
                        scope.launch {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        }
                    }
                ),
                ItemModel(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .padding(horizontal = 8.dp),
                    icon = painterResource(id = R.drawable.ic_select_from_gallery_icon),
                    title = stringResource(R.string.file),
                    type = Constants.FILE,
                    onClickCallBack = {
                        scope.launch {
                            filePickerLauncher.launch("*/*")
                        }
                    }
                ),
                ItemModel(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .padding(start = 8.dp),
                    icon = painterResource(id = R.drawable.ic_select_from_camera_icon),
                    title = stringResource(R.string.open_camera),
                    type = Constants.CAMERA,
                    onClickCallBack = {
                        scope.launch {
                            val uri = createImageUri(context)
                            cameraUri = uri
                            cameraLauncher.launch(uri)
                        }
                    }
                ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(dataList.size) { index ->
                    Item(data = dataList[index])
                }
            }
        }
    }
}

@Keep
data class ItemModel(
    val modifier: Modifier,
    val icon: Painter,
    val title: String,
    val type: Int,
    val onClickCallBack: (Int) -> Unit
)

@Preview
@Composable
private fun Item(
    data: ItemModel = ItemModel(
        modifier = Modifier,
        icon = painterResource(id = R.drawable.ic_select_from_gallery_icon),
        title = stringResource(R.string.photo_gallery),
        type = Constants.FILE,
        onClickCallBack = {}
    )
) {
    Card(
        modifier = data.modifier,
        shape = RoundedCornerShape(size = 16.dp),
        border = BorderStroke(width = (0.5).dp, color = Color(color = 0xFFD5ECE2)),
        colors = CardDefaults.cardColors(containerColor = Color(color = 0xFFF0F9F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    data.onClickCallBack(data.type)
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
                    painter = data.icon,
                    contentDescription = null
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                minLines = 2,
                maxLines = 2,
                text = data.title,
                color = Color.Black,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}