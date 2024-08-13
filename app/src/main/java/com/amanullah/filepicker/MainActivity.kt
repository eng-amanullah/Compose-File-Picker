package com.amanullah.filepicker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.amanullah.filepicker.customview.ChooseFromGalleryAndCamera
import com.amanullah.filepicker.customview.CustomDialog
import com.amanullah.filepicker.ui.theme.FilePickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilePickerTheme {

                var showDialog by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        if (showDialog) {
                            CustomDialog(
                                showDialog = true,
                                enableOutSideClick = false,
                                content = {
                                    ChooseFromGalleryAndCamera(
                                        galleryButtonCallBack = { file ->
                                            Log.e("GALLERY_BUTTON_CALL_BACK", file.toString())

                                            showDialog = false
                                        },
                                        cameraButtonCallBack = { file ->
                                            Log.e("CAMERA_BUTTON_CALL_BACK", file.toString())

                                            showDialog = false
                                        },
                                        skipButtonCallBak = {
                                            showDialog = false
                                        }
                                    )
                                },
                                onClick = {
                                    showDialog = false
                                }
                            )
                        } else {
                            Button(
                                onClick = {
                                    showDialog = true
                                }
                            ) {
                                Text(text = stringResource(R.string.upload_image))
                            }
                        }
                    }
                }
            }
        }
    }
}