package com.livingtechusa.reflexion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.livingtechusa.reflexion.ui.theme.selectableColorSchemes.returnSecondaryColors
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import kotlin.reflect.KFunction1
@Composable
fun MaterialRadioButtonGroupComponent(
    options: Map<String, Int>,
    selection: KFunction1<Int, Unit>,
) {
    var selected by remember { mutableStateOf(EMPTY_STRING) }
    val border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
    OutlinedCard(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        border = border,
        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 8.dp)
    ) {
        val onSelectedChange = { entry: Map.Entry<String, Int> ->
            selected = entry.key
            selection(entry.value)
        }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .verticalScroll(rememberScrollState())
        ) {
            options.forEach { pair ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (pair.key == selected),
                            onClick = { onSelectedChange(pair) })
                        .padding(16.dp)
                ) {
                    RadioButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        selected = (pair.key == selected),
                        onClick = { onSelectedChange(pair) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedColor = MaterialTheme.colorScheme.outline
                        )
                    )
                    OutlinedCard(
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        border = border,
                        elevation = CardDefaults.outlinedCardElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = pair.key,
                            color = returnSecondaryColors(pair.value).second,
                            style = MaterialTheme.typography.bodyLarge.merge(),
                            modifier = Modifier
                                .background(returnSecondaryColors(pair.value).first)
                                .padding(16.dp)
                                .fillMaxSize(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
