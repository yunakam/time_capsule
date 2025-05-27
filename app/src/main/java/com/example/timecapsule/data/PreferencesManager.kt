package com.example.timecapsule.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timecapsule.ui.components.TopPageSetting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val TOP_PAGE_SETTING = stringPreferencesKey("top_page_setting")
    }

    val topPageSettingFlow: Flow<TopPageSetting> = context.dataStore.data
        .map { preferences ->
            val settingName = preferences[TOP_PAGE_SETTING] ?: TopPageSetting.ADD_NOTE_DIALOG.name
            Log.d("PreferencesManager", "Reading topPageSetting: $settingName")
            try {
                TopPageSetting.valueOf(settingName)
            } catch (e: IllegalArgumentException) {
                Log.e("PreferencesManager", "Invalid setting value: $settingName, defaulting to ADD_NOTE_DIALOG")
                TopPageSetting.ADD_NOTE_DIALOG
            }
        }

    suspend fun setTopPageSetting(setting: TopPageSetting) {
        Log.d("PreferencesManager", "Setting topPageSetting to: ${setting.name}")
        context.dataStore.edit { preferences ->
            preferences[TOP_PAGE_SETTING] = setting.name
        }
    }
}
