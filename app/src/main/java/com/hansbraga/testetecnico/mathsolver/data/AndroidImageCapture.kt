package com.hansbraga.testetecnico.mathsolver.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File

private const val MAX_IMAGE_DIMENSION = 1024
private const val JPEG_QUALITY = 80

class AndroidImageCapture(private val context: Context) : ImageCapture {

    override fun createCaptureUri(): Uri {
        val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
        val file = File(imagesDir, "capture_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    override fun compress(uri: Uri): ByteArray? {
        val original = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
            ?: return null

        val (targetWidth, targetHeight) = scaledDimensions(original.width, original.height, MAX_IMAGE_DIMENSION)
        val bitmap = if (targetWidth != original.width || targetHeight != original.height) {
            Bitmap.createScaledBitmap(original, targetWidth, targetHeight, true)
        } else {
            original
        }

        return ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
            stream.toByteArray()
        }
    }
}

internal fun scaledDimensions(width: Int, height: Int, maxDimension: Int): Pair<Int, Int> {
    val scale = maxDimension.toFloat() / maxOf(width, height)
    return if (scale < 1f) {
        (width * scale).toInt() to (height * scale).toInt()
    } else {
        width to height
    }
}
