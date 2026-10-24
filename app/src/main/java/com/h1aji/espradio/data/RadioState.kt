package com.h1aji.espradio.data

/** Bass gain: Off, +1dB .. +15dB (16 steps). */
val BASS_GAIN_LABELS = listOf("Off") + (1..15).map { "+${it}dB" }

/** Bass frequency: 10 Hz .. 140 Hz in steps of 10 Hz (14 steps). */
val BASS_FREQ_LABELS = (1..14).map { "${it * 10} Hz" }

/** Treble gain: -12dB .. +10.5dB in steps of 1.5dB (16 steps). */
val TREBLE_GAIN_LABELS = (0..15).map { i ->
    val tenths = -120 + i * 15
    val sign = if (tenths >= 0) "+" else ""
    "$sign${tenths / 10}.${kotlin.math.abs(tenths % 10)}dB"
}

/** Treble frequency: 1 kHz .. 15 kHz in steps of 1 kHz (15 steps). */
val TREBLE_FREQ_LABELS = (1..15).map { "$it kHz" }

data class RadioUiState(
    val deviceIp: String = "",
    val isConnecting: Boolean = false,
    val statusText: String = "Not connected",
    val errorMessage: String? = null,
    val isPlaying: Boolean = true,
    val volume: Int = 50,
    val presetList: List<String> = emptyList(),
    val stationInput: String = "",
    val bassGainIndex: Int = 0,
    val bassFreqIndex: Int = 0,
    val trebleGainIndex: Int = 0,
    val trebleFreqIndex: Int = 0,
)
