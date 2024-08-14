package com.amanullah.filepicker.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Uri.uriToFile(context: Context): File? {
    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(this)
    val file = File(context.cacheDir, this.getFileName(context))

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

fun Uri.getFileName(context: Context): String {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    val name = cursor?.getColumnIndex("_display_name")?.let { columnIndex ->
        cursor.moveToFirst()
        cursor.getString(columnIndex)
    }
    cursor?.close()
    return name ?: "unknown_file"
}

fun File.fileToUri(context: Context): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider", // The authority string defined in the manifest
        this
    )
}

fun createImageUri(context: Context): Uri {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileProvider",
        file
    )
}