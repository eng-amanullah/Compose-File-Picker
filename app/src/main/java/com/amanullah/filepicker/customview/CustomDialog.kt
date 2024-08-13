package com.amanullah.filepicker.customview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog

@Preview
@Composable
fun CustomDialog(
    showDialog: Boolean = true,
    enableOutSideClick: Boolean = false,
    content: @Composable () -> Unit = {},
    onClick: () -> Unit = {}
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { if (enableOutSideClick) onClick() }
        ) {
            content()
        }
    }
}