package com.amanullah.filepicker

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.amanullah.filepicker.customview.ChooseMultipleItemFromGalleryAndCamera
import com.amanullah.filepicker.customview.ChooseSingleItemFromGalleryAndCamera
import com.amanullah.filepicker.customview.CustomDialog
import com.amanullah.filepicker.customview.DisplayImageFromFile
import com.amanullah.filepicker.ui.theme.FilePickerTheme
import com.amanullah.filepicker.utils.Constants
import com.amanullah.filepicker.utils.filterImageFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilePickerTheme {

                var showingType by remember { mutableStateOf(Constants.DIALOG) }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                ) { innerPadding ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(shape = RoundedCornerShape(size = 16.dp))
                    ) {
                        Text(
                            modifier = Modifier
                                .weight(weight = 1f, fill = true)
                                .background(color = if (showingType == Constants.DIALOG) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified)
                                .clickable {
                                    showingType = Constants.DIALOG
                                }
                                .padding(16.dp),
                            text = stringResource(R.string.dialog),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            modifier = Modifier
                                .weight(weight = 1f, fill = true)
                                .background(color = if (showingType == Constants.BOTTOM_SHEET) MaterialTheme.colorScheme.primaryContainer else Color.Unspecified)
                                .clickable {
                                    showingType = Constants.BOTTOM_SHEET
                                }
                                .padding(16.dp),
                            text = stringResource(R.string.bottom_sheet),
                            textAlign = TextAlign.Center
                        )
                    }

                    when (showingType) {
                        Constants.DIALOG -> {
                            InitViewWithDialog(innerPadding = innerPadding)
                        }

                        Constants.BOTTOM_SHEET -> {
                            InitViewWithBottomSheet(innerPadding = innerPadding)
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InitViewWithDialog(
        innerPadding: PaddingValues
    ) {
        var chooseItem by remember { mutableStateOf(value = "") }
        var showDialog by remember { mutableStateOf(value = false) }
        var imageFiles by remember { mutableStateOf<List<File>?>(value = null) }

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
                        when (chooseItem) {
                            Constants.SINGLE_ITEM -> {
                                ChooseSingleItemFromGalleryAndCamera(
                                    onFileSelected = { file ->
                                        Log.e("SINGLE_ITEM_CALL_BACK", file.toString())

                                        imageFiles =
                                            filterImageFiles(files = mutableListOf(file))

                                        showDialog = false
                                    },
                                    onSkip = {
                                        showDialog = false
                                    }
                                )
                            }

                            Constants.MULTIPLE_ITEM -> {
                                ChooseMultipleItemFromGalleryAndCamera(
                                    onFilesSelected = { file ->
                                        Log.e(
                                            "MULTIPLE_ITEM_CALL_BACK",
                                            file.toString()
                                        )

                                        imageFiles = filterImageFiles(files = file)

                                        showDialog = false
                                    },
                                    onSkip = {
                                        showDialog = false
                                    }
                                )
                            }
                        }
                    },
                    onClick = {
                        showDialog = false
                    }
                )
            } else {
                imageFiles?.let { itemList ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .horizontalScroll(state = rememberScrollState()),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(itemList.size) { index ->
                            DisplayImageFromFile(
                                modifier = Modifier
                                    .size(width = 150.dp, height = 200.dp)
                                    .padding(
                                        start = if (itemList[index] == itemList.last()) 8.dp else 8.dp,
                                        end = if (itemList[index] == itemList.first()) 8.dp else 8.dp
                                    ),
                                file = itemList[index]
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        chooseItem = Constants.SINGLE_ITEM
                        showDialog = true
                    }
                ) {
                    Text(text = stringResource(R.string.upload_single_image))
                }

                Button(
                    onClick = {
                        chooseItem = Constants.MULTIPLE_ITEM
                        showDialog = true
                    }
                ) {
                    Text(text = stringResource(R.string.upload_multiple_image))
                }
            }
        }
    }

    @SuppressLint("MutableCollectionMutableState")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun InitViewWithBottomSheet(
        innerPadding: PaddingValues
    ) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val scope = rememberCoroutineScope()

        var chooseItem by remember { mutableStateOf(Constants.SINGLE_ITEM) }
        var imageFiles by remember { mutableStateOf<MutableList<File>?>(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            imageFiles?.let { itemList ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center
                ) {
                    itemList.forEach { file ->
                        DisplayImageFromFile(
                            modifier = Modifier
                                .size(150.dp, 200.dp)
                                .padding(horizontal = 8.dp),
                            file = file
                        )
                    }
                }
            }

            Button(
                onClick = {
                    chooseItem = Constants.SINGLE_ITEM
                    scope.launch { sheetState.show() }
                }
            ) {
                Text(text = stringResource(R.string.upload_single_image))
            }

            Button(
                onClick = {
                    chooseItem = Constants.MULTIPLE_ITEM
                    scope.launch { sheetState.show() }
                }
            ) {
                Text(text = stringResource(R.string.upload_multiple_image))
            }

            if (sheetState.isVisible) {
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                        }
                    }
                ) {
                    when (chooseItem) {
                        Constants.SINGLE_ITEM -> {
                            ChooseSingleItemFromGalleryAndCamera(
                                onFileSelected = { file ->
                                    scope.launch(Dispatchers.IO) {
                                        val filteredFiles = filterImageFiles(mutableListOf(file))
                                        withContext(Dispatchers.Main) {
                                            imageFiles = filteredFiles.toMutableList()
                                            sheetState.hide()
                                        }
                                    }
                                },
                                onSkip = {
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                }
                            )
                        }

                        Constants.MULTIPLE_ITEM -> {
                            ChooseMultipleItemFromGalleryAndCamera(
                                onFilesSelected = { files ->
                                    scope.launch(Dispatchers.IO) {
                                        val filteredFiles = filterImageFiles(files.toMutableList())
                                        withContext(Dispatchers.Main) {
                                            imageFiles = filteredFiles.toMutableList()
                                            sheetState.hide()
                                        }
                                    }
                                },
                                onSkip = {
                                    scope.launch {
                                        sheetState.hide()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}