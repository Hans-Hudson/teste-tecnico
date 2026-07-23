package com.hansbraga.testetecnico.mathsolver.presentation.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverIntent
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverState
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverViewModel
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream
import java.io.File

private const val MAX_IMAGE_DIMENSION = 1024
private const val JPEG_QUALITY = 80

@Composable
fun PhotoSolverScreen(
    onNavigateBack: () -> Unit,
    viewModel: PhotoSolverViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
        val uri = pendingCaptureUri
        if (captured && uri != null) {
            readCompressedImageBytes(context, uri)?.let { bytes ->
                viewModel.onIntent(PhotoSolverIntent.ImageCaptured(bytes))
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            readCompressedImageBytes(context, uri)?.let { bytes ->
                viewModel.onIntent(PhotoSolverIntent.ImageCaptured(bytes))
            }
        }
    }

    PhotoSolverScreenContent(
        state = state,
        onTakePhoto = {
            val uri = createImageCaptureUri(context)
            pendingCaptureUri = uri
            cameraLauncher.launch(uri)
        },
        onPickFromGallery = {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onReset = { viewModel.onIntent(PhotoSolverIntent.Reset) },
        onNavigateBack = onNavigateBack
    )
}

@Composable
fun PhotoSolverScreenContent(
    state: PhotoSolverState,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit,
    onReset: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val actionsEnabled = state !is PhotoSolverState.Loading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag(PhotoSolverTestTags.BACK_BUTTON)
            ) {
                Text("Voltar")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            when (state) {
                PhotoSolverState.Idle -> Text("Fotografe ou selecione uma imagem com uma expressão matemática")

                PhotoSolverState.Loading -> CircularProgressIndicator(
                    modifier = Modifier
                        .testTag(PhotoSolverTestTags.LOADING_INDICATOR)
                        .semantics { contentDescription = "Processando imagem" }
                )

                is PhotoSolverState.Success -> Text(
                    text = "${state.expression} = ${state.result}",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .testTag(PhotoSolverTestTags.RESULT_TEXT)
                        .semantics { contentDescription = "Resultado: ${state.expression} igual a ${state.result}" }
                )

                is PhotoSolverState.Error -> Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.testTag(PhotoSolverTestTags.ERROR_TEXT)
                )
            }

            if (state is PhotoSolverState.Success || state is PhotoSolverState.Error) {
                TextButton(
                    onClick = onReset,
                    modifier = Modifier.testTag(PhotoSolverTestTags.RESET_BUTTON)
                ) {
                    Text("Limpar")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onTakePhoto,
                enabled = actionsEnabled,
                modifier = Modifier.testTag(PhotoSolverTestTags.TAKE_PHOTO_BUTTON)
            ) {
                Text("Tirar foto")
            }
            Button(
                onClick = onPickFromGallery,
                enabled = actionsEnabled,
                modifier = Modifier.testTag(PhotoSolverTestTags.PICK_PHOTO_BUTTON)
            ) {
                Text("Escolher da galeria")
            }
        }
    }
}

private fun createImageCaptureUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File(imagesDir, "capture_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

private fun readCompressedImageBytes(context: Context, uri: Uri): ByteArray? {
    val original = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        ?: return null

    val scale = MAX_IMAGE_DIMENSION.toFloat() / maxOf(original.width, original.height)
    val bitmap = if (scale < 1f) {
        Bitmap.createScaledBitmap(original, (original.width * scale).toInt(), (original.height * scale).toInt(), true)
    } else {
        original
    }

    return ByteArrayOutputStream().use { stream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, stream)
        stream.toByteArray()
    }
}
