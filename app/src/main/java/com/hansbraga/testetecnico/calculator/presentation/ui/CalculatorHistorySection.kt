package com.hansbraga.testetecnico.calculator.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.hansbraga.testetecnico.calculator.domain.HistoryItem

@Composable
fun CalculatorHistorySection(
    history: List<HistoryItem>,
    onItemSelected: (Long) -> Unit,
    onItemDeleted: (Long) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isHistoryEmpty = history.isEmpty()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .alpha(if (isHistoryEmpty) 0f else 1f)
                .then(
                    if (isHistoryEmpty) Modifier.semantics { hideFromAccessibility() } else Modifier
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Histórico",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.semantics { heading() }
            )
            TextButton(
                onClick = onClearAll,
                enabled = !isHistoryEmpty,
                modifier = Modifier
                    .testTag(CalculatorTestTags.HISTORY_CLEAR_BUTTON)
                    .semantics { contentDescription = "Limpar histórico" }
            ) {
                Text("Limpar")
            }
        }
        val listState = rememberLazyListState()
        LaunchedEffect(history.size) {
            if (history.isNotEmpty()) {
                listState.animateScrollToItem(history.lastIndex)
            }
        }
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .testTag(CalculatorTestTags.HISTORY_LIST)
        ) {
            items(items = history, key = { it.id }) { item ->
                HistoryRow(item = item, onSelected = onItemSelected, onDeleted = onItemDeleted)
            }
        }
    }
}

@Composable
private fun HistoryRow(item: HistoryItem, onSelected: (Long) -> Unit, onDeleted: (Long) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected(item.id) }
            .testTag(CalculatorTestTags.historyItem(item.id))
            .semantics {
                contentDescription = "Reutilizar cálculo: ${item.expression} igual a ${item.result}"
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = item.expression, style = MaterialTheme.typography.bodySmall)
            Text(text = "= ${item.result}", style = MaterialTheme.typography.bodyLarge)
        }
        TextButton(
            onClick = { onDeleted(item.id) },
            modifier = Modifier
                .testTag(CalculatorTestTags.historyItemDelete(item.id))
                .semantics {
                    contentDescription = "Apagar cálculo ${item.expression} igual a ${item.result} do histórico"
                }
        ) {
            Text("✕")
        }
    }
}
