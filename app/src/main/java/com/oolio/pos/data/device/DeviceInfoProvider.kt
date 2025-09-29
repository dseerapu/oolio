package com.oolio.pos.data.device

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private val Context.deviceInfoDataStore by preferencesDataStore("device_info_prefs")

@Singleton
class DeviceInfoProvider @Inject constructor(
    private val context: Context
) {

    private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
    private val CLIENT_ID_KEY = stringPreferencesKey("client_id")

    suspend fun getDeviceId(): String {
        val prefs = context.deviceInfoDataStore.data.first()
        val existingId = prefs[DEVICE_ID_KEY]
        return if (existingId != null) {
            existingId
        } else {
            val newId = UUID.randomUUID().toString()
            context.deviceInfoDataStore.edit { it[DEVICE_ID_KEY] = newId }
            newId
        }
    }


    suspend fun getClientId(): String {
        val prefs = context.deviceInfoDataStore.data.first()
        val existingId = prefs[CLIENT_ID_KEY]
        return if (existingId != null) {
            existingId
        } else {
            val newId = UUID.randomUUID().toString()
            context.deviceInfoDataStore.edit { it[CLIENT_ID_KEY] = newId }
            newId
        }
    }
}
