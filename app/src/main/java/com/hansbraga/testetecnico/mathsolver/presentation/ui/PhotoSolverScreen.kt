package com.hansbraga.testetecnico.mathsolver.presentation.ui

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hansbraga.testetecnico.R
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverIntent
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverState
import com.hansbraga.testetecnico.mathsolver.presentation.mvi.PhotoSolverViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhotoSolverScreen(
    onNavigateBack: () -> Unit,
    viewModel: PhotoSolverViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var pendingCaptureUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
        val uri = pendingCaptureUri
        if (captured && uri != null) {
            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(uri))
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.onIntent(PhotoSolverIntent.ImageCaptured(uri))
        }
    }

    PhotoSolverScreenContent(
        state = state,
        onTakePhoto = {
            val uri = viewModel.createCaptureUri()
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
                Text(stringResource(R.string.photo_solver_back_label))
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
                PhotoSolverState.Idle -> Text(stringResource(R.string.photo_solver_idle_message))

                PhotoSolverState.Loading -> {
                    val loadingDescription = stringResource(R.string.photo_solver_loading_description)
                    CircularProgressIndicator(
                        modifier = Modifier
                            .testTag(PhotoSolverTestTags.LOADING_INDICATOR)
                            .semantics { contentDescription = loadingDescription }
                    )
                }

                is PhotoSolverState.Success -> {
                    val resultDescription =
                        stringResource(R.string.photo_solver_result_description, state.expression, state.result)
                    Text(
                        text = stringResource(R.string.photo_solver_result_text, state.expression, state.result),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier
                            .testTag(PhotoSolverTestTags.RESULT_TEXT)
                            .semantics { contentDescription = resultDescription }
                    )
                }

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
                    Text(stringResource(R.string.photo_solver_reset_label))
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
                Text(stringResource(R.string.photo_solver_take_photo_label))
            }
            Button(
                onClick = onPickFromGallery,
                enabled = actionsEnabled,
                modifier = Modifier.testTag(PhotoSolverTestTags.PICK_PHOTO_BUTTON)
            ) {
                Text(stringResource(R.string.photo_solver_pick_gallery_label))
            }
        }
    }
}
