package com.h1aji.espradio.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "esp_radio_settings")

/** Replaces the original app's TinyDB keys: ip, ip1..ip5, ip_latest. */
class SettingsRepository(private val context: Context) {

    private val activeIpKey = stringPreferencesKey("ip")
    private val slotKeys = (1..SLOT_COUNT).map { stringPreferencesKey("ip$it") }

    val activeIp: Flow<String> = context.dataStore.data.map { it[activeIpKey].orEmpty() }

    val savedIps: Flow<List<String>> = context.dataStore.data.map { prefs ->
        slotKeys.map { key -> prefs[key].orEmpty() }
    }

    suspend fun setActiveIp(ip: String) {
        context.dataStore.edit { it[activeIpKey] = ip }
    }

    suspend fun setSavedIp(slot: Int, ip: String) {
        require(slot in 1..SLOT_COUNT) { "slot must be 1..$SLOT_COUNT" }
        context.dataStore.edit { it[slotKeys[slot - 1]] = ip }
    }

    companion object {
        const val SLOT_COUNT = 5
    }
}
