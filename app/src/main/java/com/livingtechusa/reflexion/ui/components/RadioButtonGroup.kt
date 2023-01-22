package com.livingtechusa.reflexion.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.livingtechusa.reflexion.ui.theme.selectableColorSchemes.returnPrimaryColors
import com.livingtechusa.reflexion.util.Constants.EMPTY_STRING
import kotlin.reflect.KFunction1


// We represent a Composable function by annotating it with the @Composable annotation. Composable
// functions can only be called from within the scope of other composable functions. We should
// think of composable functions to be similar to lego blocks - each composable function is in turn
// built up of smaller composable functions.
@Composable
fun MaterialRadioButtonGroupComponent(
    options: Map<String, Int>,
    selection: KFunction1<Int, Unit>,
) {
    // Reacting to state changes is the core behavior of Compose. You will notice a couple new
    // keywords that are compose related - remember & mutableStateOf.remember{} is a helper
    // composable that calculates the value passed to it only during the first composition. It then
    // returns the same value for every subsequent composition. Next, you can think of
    // mutableStateOf as an observable value where updates to this variable will redraw all
    // the composable functions that access it. We don't need to explicitly subscribe at all. Any
    // composable that reads its value will be recomposed any time the value
    // changes. This ensures that only the composables that depend on this will be redraw while the
    // rest remain unchanged. This ensures efficiency and is a performance optimization. It
    // is inspired from existing frameworks like React.
    var selected by remember { mutableStateOf(EMPTY_STRING) }
    // Card composable is a predefined composable that is meant to represent the card surface as
    // specified by the Material Design specification. We also configure it to have rounded
    // corners and apply a modifier.

    // You can think of Modifiers as implementations of the decorators pattern that are used to
    // modify the composable that its applied to. In the example below, we add a padding of
    // 8dp to the Card composable. In addition, we configure it out occupy the entire available
    // width using the Modifier.fillMaxWidth() modifier.
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    OutlinedCard(
        shape = MaterialTheme.shapes.large,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        border = border,
        elevation = CardDefaults.outlinedCardElevation()
    ) {
        // A pre-defined composable that's capable of rendering a radio group. It honors the
        // Material Design specification.
        val onSelectedChange = { entry: Map.Entry<String, Int> ->
            selected = entry.key
            selection(entry.value)
        }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            options.forEach { pair ->
                Row(Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (pair.key == selected),
                        onClick = { onSelectedChange(pair) }
                    )
                    .padding(16.dp)
                ) {
                    RadioButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        selected = (pair.key == selected),
                        onClick = { onSelectedChange(pair) }
                    )
                    Card(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = pair.key,
                            color = returnPrimaryColors(pair.value).second,
                            style = MaterialTheme.typography.bodyLarge.merge(),
                            modifier = Modifier
                                .background(returnPrimaryColors(pair.value).first)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
