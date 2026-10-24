package com.h1aji.espradio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.h1aji.espradio.ui.ControlScreen
import com.h1aji.espradio.ui.SettingsScreen
import com.h1aji.espradio.ui.theme.ESPRadioTheme
import com.h1aji.espradio.viewmodel.ControlViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ESPRadioTheme {
                val viewModel: ControlViewModel = viewModel()
                var showSettings by remember { mutableStateOf(false) }
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                BackHandler(enabled = showSettings) { showSettings = false }

                if (showSettings) {
                    SettingsScreen(onBack = { showSettings = false })
                } else {
                    ControlScreen(
                        state = state,
                        onStartPolling = viewModel::startPolling,
                        onStopPolling = viewModel::stopPolling,
                        onVolumeDown = viewModel::volumeDown,
                        onVolumeUp = viewModel::volumeUp,
                        onVolumeChange = viewModel::setVolume,
                        onPresetPrev = viewModel::presetPrev,
                        onPresetNext = viewModel::presetNext,
                        onTogglePlay = viewModel::togglePlay,
                        onRefreshStatus = viewModel::refreshStatus,
                        onRefreshPresetList = viewModel::refreshPresetList,
                        onSelectPreset = viewModel::selectPreset,
                        onStationInputChange = viewModel::updateStationInput,
                        onPlayStation = viewModel::playCustomStation,
                        onBassGainChange = viewModel::setBassGain,
                        onBassFreqChange = viewModel::setBassFreq,
                        onTrebleGainChange = viewModel::setTrebleGain,
                        onTrebleFreqChange = viewModel::setTrebleFreq,
                        onSettingsClick = { showSettings = true },
                    )
                }
            }
        }
    }
}
