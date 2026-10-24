package com.h1aji.espradio.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/** A label + value field that opens a dropdown of [options], driving a tone control. */
@Composable
fun ToneDropdownField(
    label: String,
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }
    val safeIndex = selectedIndex.coerceIn(0, options.lastIndex)
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = options.getOrElse(safeIndex) { label },
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(index)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
fun ToneControlsSection(
    bassGainOptions: List<String>,
    bassFreqOptions: List<String>,
    trebleGainOptions: List<String>,
    trebleFreqOptions: List<String>,
    bassGainIndex: Int,
    bassFreqIndex: Int,
    trebleGainIndex: Int,
    trebleFreqIndex: Int,
    onBassGainChange: (Int) -> Unit,
    onBassFreqChange: (Int) -> Unit,
    onTrebleGainChange: (Int) -> Unit,
    onTrebleFreqChange: (Int) -> Unit,
) {
    Column {
        SectionLabel("Bass")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ToneDropdownField(
                label = "Bass Gain",
                options = bassGainOptions,
                selectedIndex = bassGainIndex,
                onSelect = onBassGainChange,
                modifier = Modifier.weight(1f),
            )
            ToneDropdownField(
                label = "Bass Freq",
                options = bassFreqOptions,
                selectedIndex = bassFreqIndex,
                onSelect = onBassFreqChange,
                modifier = Modifier.weight(1f),
            )
        }
        SectionLabel("Treble", modifier = Modifier.padding(top = 8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            ToneDropdownField(
                label = "Treble Gain",
                options = trebleGainOptions,
                selectedIndex = trebleGainIndex,
                onSelect = onTrebleGainChange,
                modifier = Modifier.weight(1f),
            )
            ToneDropdownField(
                label = "Treble Freq",
                options = trebleFreqOptions,
                selectedIndex = trebleFreqIndex,
                onSelect = onTrebleFreqChange,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
