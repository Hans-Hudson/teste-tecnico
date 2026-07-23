package com.hansbraga.testetecnico.calculator.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hansbraga.testetecnico.calculator.domain.CalculatorOperation
import com.hansbraga.testetecnico.calculator.domain.HistoryItem
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorIntent
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorState
import com.hansbraga.testetecnico.calculator.presentation.mvi.CalculatorViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalculatorScreen(
    onOpenPhotoSolver: () -> Unit,
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val history by viewModel.historyState.collectAsStateWithLifecycle()
    CalculatorScreenContent(
        state = state,
        history = history,
        onIntent = viewModel::onIntent,
        onOpenPhotoSolver = onOpenPhotoSolver
    )
}

@Composable
fun CalculatorScreenContent(
    state: CalculatorState,
    history: List<HistoryItem>,
    onIntent: (CalculatorIntent) -> Unit,
    onOpenPhotoSolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onOpenPhotoSolver,
                modifier = Modifier
                    .testTag(CalculatorTestTags.OPEN_PHOTO_SOLVER_BUTTON)
                    .semantics { contentDescription = "Resolver expressão por foto" }
            ) {
                Text("Resolver por foto")
            }
        }

        CalculatorHistorySection(
            history = history,
            onItemSelected = { id -> onIntent(CalculatorIntent.HistoryItemSelected(id)) },
            onItemDeleted = { id -> onIntent(CalculatorIntent.DeleteHistoryItem(id)) },
            onClearAll = { onIntent(CalculatorIntent.ClearHistoryPressed) }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(24.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = state.display,
                modifier = Modifier
                    .testTag(CalculatorTestTags.DISPLAY)
                    .semantics {
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = if (state.isError) {
                            state.display
                        } else {
                            "Resultado: ${state.display}"
                        }
                    },
                fontSize = 56.sp,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (state.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
            )
        }

        val rows = listOf(
            listOf(ButtonSpec.Clear, ButtonSpec.ToggleSign, ButtonSpec.Percent, ButtonSpec.Operation(CalculatorOperation.DIVIDE)),
            listOf(ButtonSpec.Digit(7), ButtonSpec.Digit(8), ButtonSpec.Digit(9), ButtonSpec.Operation(CalculatorOperation.MULTIPLY)),
            listOf(ButtonSpec.Digit(4), ButtonSpec.Digit(5), ButtonSpec.Digit(6), ButtonSpec.Operation(CalculatorOperation.SUBTRACT)),
            listOf(ButtonSpec.Digit(1), ButtonSpec.Digit(2), ButtonSpec.Digit(3), ButtonSpec.Operation(CalculatorOperation.ADD)),
            listOf(ButtonSpec.Digit(0), ButtonSpec.Decimal, ButtonSpec.Equals)
        )

        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { spec ->
                    CalculatorButton(
                        spec = spec,
                        weight = if (spec is ButtonSpec.Digit && spec.digit == 0) 2f else 1f,
                        onIntent = onIntent
                    )
                }
            }
        }
    }
}

private sealed interface ButtonSpec {
    data class Digit(val digit: Int) : ButtonSpec
    data class Operation(val operation: CalculatorOperation) : ButtonSpec
    data object Decimal : ButtonSpec
    data object Equals : ButtonSpec
    data object Clear : ButtonSpec
    data object ToggleSign : ButtonSpec
    data object Percent : ButtonSpec
}

@Composable
private fun RowScope.CalculatorButton(
    spec: ButtonSpec,
    weight: Float,
    onIntent: (CalculatorIntent) -> Unit
) {
    val (label, description, tag, intent, colors) = when (spec) {
        is ButtonSpec.Digit -> ButtonPresentation(
            label = spec.digit.toString(),
            description = null,
            tag = CalculatorTestTags.digitButton(spec.digit),
            intent = CalculatorIntent.DigitPressed(spec.digit),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        is ButtonSpec.Operation -> ButtonPresentation(
            label = spec.operation.symbol,
            description = spec.operation.accessibilityLabel,
            tag = CalculatorTestTags.operationButton(spec.operation),
            intent = CalculatorIntent.OperationPressed(spec.operation),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        ButtonSpec.Decimal -> ButtonPresentation(
            label = ".",
            description = "Ponto decimal",
            tag = CalculatorTestTags.DECIMAL_BUTTON,
            intent = CalculatorIntent.DecimalPointPressed,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        ButtonSpec.Equals -> ButtonPresentation(
            label = "=",
            description = "Igual",
            tag = CalculatorTestTags.EQUALS_BUTTON,
            intent = CalculatorIntent.EqualsPressed,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        ButtonSpec.Clear -> ButtonPresentation(
            label = "C",
            description = "Limpar calculadora",
            tag = CalculatorTestTags.CLEAR_BUTTON,
            intent = CalculatorIntent.ClearPressed,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        )

        ButtonSpec.ToggleSign -> ButtonPresentation(
            label = "+/-",
            description = "Alternar sinal",
            tag = CalculatorTestTags.TOGGLE_SIGN_BUTTON,
            intent = CalculatorIntent.ToggleSignPressed,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        )

        ButtonSpec.Percent -> ButtonPresentation(
            label = "%",
            description = "Porcentagem",
            tag = CalculatorTestTags.PERCENT_BUTTON,
            intent = CalculatorIntent.PercentPressed,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary
            )
        )
    }

    Button(
        onClick = { onIntent(intent) },
        colors = colors,
        modifier = Modifier
            .weight(weight)
            .aspectRatio(if (weight > 1f) 2f else 1f)
            .testTag(tag)
            .then(
                if (description != null) {
                    Modifier.semantics { contentDescription = description }
                } else {
                    Modifier
                }
            )
    ) {
        Text(text = label, fontSize = 22.sp)
    }
}

private data class ButtonPresentation(
    val label: String,
    val description: String?,
    val tag: String,
    val intent: CalculatorIntent,
    val colors: ButtonColors
)
