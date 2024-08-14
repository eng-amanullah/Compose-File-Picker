package com.amanullah.filepicker.model

import androidx.annotation.Keep
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Keep
data class ItemModel(
    val modifier: Modifier,
    val icon: Painter,
    val title: String,
    val type: Int,
    val onClickCallBack: (Int) -> Unit
)