package com.h1aji.espradio.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * HTTP client for the ESP VS1053 Webradio firmware (ESP8266/ESP32 + VS1053 codec).
 * The device has no JSON API: every control is a plain GET with a query param,
 * and responses are plain text. All calls run on [Dispatchers.IO].
 */
class EspRadioClient {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    /** Firmware replies to any volume command with "Volume now is N" (N = 0..100). */
    suspend fun volumeDown(deviceIp: String): Int? = parseVolume(get(deviceIp, "downvolume=2"))
    suspend fun volumeUp(deviceIp: String): Int? = parseVolume(get(deviceIp, "upvolume=2"))
    suspend fun setVolume(deviceIp: String, level: Int): Int? =
        parseVolume(get(deviceIp, "volume=${level.coerceIn(0, 100)}"))
    suspend fun presetPrev(deviceIp: String) = get(deviceIp, "downpreset=1")
    suspend fun presetNext(deviceIp: String) = get(deviceIp, "uppreset=1")
    suspend fun pause(deviceIp: String) = get(deviceIp, "stop=0")
    suspend fun resume(deviceIp: String) = get(deviceIp, "resume=0")
    suspend fun selectPreset(deviceIp: String, index: Int) = get(deviceIp, "preset=$index")
    suspend fun playStation(deviceIp: String, station: String) =
        get(deviceIp, "station=${station.lowercase()}")

    suspend fun status(deviceIp: String): String = get(deviceIp, "status")

    /** Response is expected to be a '|'-delimited list of preset/station names. */
    suspend fun presetList(deviceIp: String): List<String> {
        val raw = get(deviceIp, "list")
        return raw.split("|").map { it.trim() }.filter { it.isNotEmpty() }
    }

    // Tone controls (VS1053 bass/treble registers). The exact value semantics expected by the
    // firmware (raw index vs. Hz/dB units) could not be confirmed without a physical device -
    // these send the slider's numeric value as-is; adjust if a real device expects something else.
    suspend fun setBassGain(deviceIp: String, value: Int) = get(deviceIp, "tonela=$value")
    suspend fun setBassFreq(deviceIp: String, value: Int) = get(deviceIp, "tonelf=$value")
    suspend fun setTrebleGain(deviceIp: String, value: Int) = get(deviceIp, "toneha=$value")
    suspend fun setTrebleFreq(deviceIp: String, value: Int) = get(deviceIp, "tonehf=$value")

    private fun parseVolume(reply: String): Int? =
        Regex("is\\s+(\\d+)").find(reply)?.groupValues?.get(1)?.toIntOrNull()

    private suspend fun get(deviceIp: String, query: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://$deviceIp/?$query")
            .build()
        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw java.io.IOException("HTTP ${response.code}")
            }
            response.body?.string()?.trim().orEmpty()
        }
    }
}
