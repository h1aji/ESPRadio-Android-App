package com.h1aji.espradio.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.h1aji.espradio.data.BASS_FREQ_LABELS
import com.h1aji.espradio.data.BASS_GAIN_LABELS
import com.h1aji.espradio.data.RadioUiState
import com.h1aji.espradio.data.TREBLE_FREQ_LABELS
import com.h1aji.espradio.data.TREBLE_GAIN_LABELS
import com.h1aji.espradio.ui.components.EspRadioTopBar
import com.h1aji.espradio.ui.components.PresetRow
import com.h1aji.espradio.ui.components.SectionLabel
import com.h1aji.espradio.ui.components.StationInputRow
import com.h1aji.espradio.ui.components.ToneControlsSection
import com.h1aji.espradio.ui.components.VolumeRow

@Composable
fun ControlScreen(
    state: RadioUiState,
    onStartPolling: () -> Unit,
    onStopPolling: () -> Unit,
    onVolumeDown: () -> Unit,
    onVolumeUp: () -> Unit,
    onVolumeChange: (Int) -> Unit,
    onPresetPrev: () -> Unit,
    onPresetNext: () -> Unit,
    onTogglePlay: () -> Unit,
    onRefreshStatus: () -> Unit,
    onRefreshPresetList: () -> Unit,
    onSelectPreset: (Int) -> Unit,
    onStationInputChange: (String) -> Unit,
    onPlayStation: () -> Unit,
    onBassGainChange: (Int) -> Unit,
    onBassFreqChange: (Int) -> Unit,
    onTrebleGainChange: (Int) -> Unit,
    onTrebleFreqChange: (Int) -> Unit,
    onSettingsClick: () -> Unit,
) {
    DisposableEffect(Unit) {
        onStartPolling()
        onDispose { onStopPolling() }
    }

    var showPresetDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            EspRadioTopBar(
                title = "ESP Radio",
                deviceIp = state.deviceIp,
                onInfoClick = { showInfoDialog = true },
                onSettingsClick = onSettingsClick,
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SectionLabel("Status")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(state.statusText, modifier = Modifier.weight(1f))
                IconButton(onClick = onRefreshStatus) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh status")
                }
            }
            state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            SectionLabel("Preset")
            Button(
                onClick = {
                    onRefreshPresetList()
                    showPresetDialog = true
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text("Select Preset", modifier = Modifier.align(Alignment.CenterStart))
                }
            }

            SectionLabel("Volume")
            VolumeRow(
                volume = state.volume,
                onVolumeDown = onVolumeDown,
                onVolumeUp = onVolumeUp,
                onVolumeChange = onVolumeChange,
            )

            SectionLabel("Channel")
            PresetRow(
                isPlaying = state.isPlaying,
                onPrev = onPresetPrev,
                onTogglePlay = onTogglePlay,
                onNext = onPresetNext,
            )

            ToneControlsSection(
                bassGainOptions = BASS_GAIN_LABELS,
                bassFreqOptions = BASS_FREQ_LABELS,
                trebleGainOptions = TREBLE_GAIN_LABELS,
                trebleFreqOptions = TREBLE_FREQ_LABELS,
                bassGainIndex = state.bassGainIndex,
                bassFreqIndex = state.bassFreqIndex,
                trebleGainIndex = state.trebleGainIndex,
                trebleFreqIndex = state.trebleFreqIndex,
                onBassGainChange = onBassGainChange,
                onBassFreqChange = onBassFreqChange,
                onTrebleGainChange = onTrebleGainChange,
                onTrebleFreqChange = onTrebleFreqChange,
            )

            SectionLabel("Play Station/File")
            StationInputRow(
                value = state.stationInput,
                onValueChange = onStationInputChange,
            )
            Button(
                onClick = onPlayStation,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text("Play")
            }
        }
    }

    if (showPresetDialog) {
        AlertDialog(
            onDismissRequest = { showPresetDialog = false },
            title = { Text("Select preset") },
            text = {
                LazyColumn {
                    items(state.presetList.size) { index ->
                        Text(
                            text = state.presetList[index],
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectPreset(index)
                                    showPresetDialog = false
                                }
                                .padding(vertical = 12.dp),
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPresetDialog = false }) { Text("Close") }
            },
        )
    }

    if (showInfoDialog) {
        val context = LocalContext.current
        val versionName = remember {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
        }
        val githubUrl = "https://github.com/h1aji/Esp-radio"
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = { Text("ESP Radio") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Remote control application for Esp-radio and ESP32-radio")
                    Text("Version $versionName")
                    Text(
                        text = githubUrl,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl)))
                        },
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) { Text("OK") }
            },
        )
    }
}
