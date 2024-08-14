package com.amanullah.filepicker.customview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun DisplayImageFromFile(
    modifier: Modifier = Modifier,
    file: File
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(size = 16.dp))
            .background(color = Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize(),
            painter = rememberAsyncImagePainter(file),
            contentDescription = null,
            contentScale = ContentScale.Fit, // Adjust this to your needs (e.g., Fit, FillBounds)
        )
    }
}