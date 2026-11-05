package com.forum.frontend.ui.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun ChipGroup(
    options: List<Pair<Int?, String>>,
    selected: Int?,
    onSelect: (Int?) -> Unit,
) {
    Div({ classes("chip-group") }) {
        options.forEach { (value, label) ->
            Button({
                classes(*cls("chip", if (value == selected) "active" else null))
                onClick { onSelect(value) }
            }) { Text(label) }
        }
    }
}