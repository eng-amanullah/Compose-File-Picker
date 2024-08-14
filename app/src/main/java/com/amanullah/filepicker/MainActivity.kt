package com.amanullah.filepicker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import com.amanullah.filepicker.customview.ChooseFromGalleryAndCamera
import com.amanullah.filepicker.customview.CustomDialog
import com.amanullah.filepicker.customview.DisplayImageFromFile
import com.amanullah.filepicker.ui.theme.FilePickerTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FilePickerTheme {

                var showDialog by remember { mutableStateOf(value = false) }
                var imageFile by remember { mutableStateOf<File?>(value = null) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues = innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (showDialog) {
                            CustomDialog(
                                showDialog = true,
                                enableOutSideClick = false,
                                content = {
                                    ChooseFromGalleryAndCamera(
                                        buttonCallBack = { file ->
                                            Log.e("CALL_BACK", file.toString())

                                            imageFile = if (!file.toString()
                                                    .contains(other = ".pdf")
                                            ) file else null

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
                            imageFile?.let {
                                DisplayImageFromFile(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(bottom = 16.dp),
                                    file = it
                                )
                            }

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