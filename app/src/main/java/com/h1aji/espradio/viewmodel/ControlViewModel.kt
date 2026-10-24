package com.h1aji.espradio.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.h1aji.espradio.data.EspRadioClient
import com.h1aji.espradio.data.RadioUiState
import com.h1aji.espradio.data.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException

private const val STATUS_POLL_INTERVAL_MS = 5000L

class ControlViewModel(application: Application) : AndroidViewModel(application) {

    private val client = EspRadioClient()
    private val settingsRepository = SettingsRepository(application)

    private val _uiState = MutableStateFlow(RadioUiState())
    val uiState: StateFlow<RadioUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        viewModelScope.launch {
            settingsRepository.activeIp.collect { ip ->
                val changed = ip != _uiState.value.deviceIp
                _uiState.update { it.copy(deviceIp = ip) }
                if (changed && ip.isNotBlank()) {
                    refreshStatus()
                    refreshPresetList()
                }
            }
        }
    }

    fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob = viewModelScope.launch {
            while (isActive) {
                delay(STATUS_POLL_INTERVAL_MS)
                refreshStatus()
            }
        }
    }

    fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    fun refreshStatus() = runIfDeviceConfigured { ip ->
        val status = client.status(ip)
        _uiState.update { it.copy(statusText = status.ifBlank { "No response" }, errorMessage = null) }
    }

    fun refreshPresetList() = runIfDeviceConfigured { ip ->
        val presets = client.presetList(ip)
        _uiState.update { it.copy(presetList = presets) }
    }

    fun volumeDown() = runIfDeviceConfigured { ip ->
        client.volumeDown(ip)?.let { newVolume -> _uiState.update { it.copy(volume = newVolume) } }
    }

    fun volumeUp() = runIfDeviceConfigured { ip ->
        client.volumeUp(ip)?.let { newVolume -> _uiState.update { it.copy(volume = newVolume) } }
    }

    fun setVolume(level: Int) = runIfDeviceConfigured { ip ->
        val newVolume = client.setVolume(ip, level)
        _uiState.update { it.copy(volume = newVolume ?: level) }
    }

    fun presetPrev() = runIfDeviceConfigured { client.presetPrev(it); refreshStatus() }
    fun presetNext() = runIfDeviceConfigured { client.presetNext(it); refreshStatus() }

    fun togglePlay() = runIfDeviceConfigured { ip ->
        val wasPlaying = _uiState.value.isPlaying
        if (wasPlaying) client.pause(ip) else client.resume(ip)
        _uiState.update { it.copy(isPlaying = !wasPlaying) }
    }

    fun selectPreset(index: Int) = runIfDeviceConfigured { ip ->
        client.selectPreset(ip, index)
        refreshStatus()
    }

    fun updateStationInput(text: String) {
        _uiState.update { it.copy(stationInput = text) }
    }

    fun playCustomStation() = runIfDeviceConfigured { ip ->
        val station = _uiState.value.stationInput
        if (station.isNotBlank()) {
            client.playStation(ip, station)
            refreshStatus()
        }
    }

    fun setBassGain(index: Int) = runIfDeviceConfigured { ip ->
        client.setBassGain(ip, index)
        _uiState.update { it.copy(bassGainIndex = index) }
    }

    fun setBassFreq(index: Int) = runIfDeviceConfigured { ip ->
        client.setBassFreq(ip, index)
        _uiState.update { it.copy(bassFreqIndex = index) }
    }

    fun setTrebleGain(index: Int) = runIfDeviceConfigured { ip ->
        client.setTrebleGain(ip, index)
        _uiState.update { it.copy(trebleGainIndex = index) }
    }

    fun setTrebleFreq(index: Int) = runIfDeviceConfigured { ip ->
        client.setTrebleFreq(ip, index)
        _uiState.update { it.copy(trebleFreqIndex = index) }
    }

    private fun runIfDeviceConfigured(action: suspend (String) -> Unit) {
        val ip = _uiState.value.deviceIp
        if (ip.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Set the device IP in Settings first") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isConnecting = true) }
            try {
                action(ip)
                _uiState.update { it.copy(errorMessage = null) }
            } catch (e: IOException) {
                _uiState.update { it.copy(errorMessage = "Couldn't reach device: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isConnecting = false) }
            }
        }
    }
}
