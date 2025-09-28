package com.oolio.pos.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "pos_sync_prefs")

class LastSyncStore @Inject constructor(@ApplicationContext private val context: Context) {
    companion object{
        private val LAST_SYNC_KEY = longPreferencesKey("last_sync_time_stamp")
    }

    suspend fun getLastSync(): Long{
        val prefs = context.dataStore.data.first()
        return prefs[LAST_SYNC_KEY] ?: 0L
    }

    suspend fun setLastSync(timeStamp: Long){
        context.dataStore.edit { prefs->
            prefs[LAST_SYNC_KEY] = timeStamp
        }
    }
}