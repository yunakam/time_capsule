package com.example.timecapsule.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.compose.ThemeType
import com.example.timecapsule.ui.components.OnLaunch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {
    companion object {
        private val UPON_LAUNCH = stringPreferencesKey("upon_launch")
        private val THEME_TYPE_KEY = stringPreferencesKey("theme_type")
    }

    val topPageSettingFlow: Flow<OnLaunch> = context.dataStore.data
        .map { preferences ->
            val settingName = preferences[UPON_LAUNCH] ?: OnLaunch.ADD_NOTE_DIALOG.name
            Log.d("PreferencesManager", "Reading topPageSetting: $settingName")
            try {
                OnLaunch.valueOf(settingName)
            } catch (e: IllegalArgumentException) {
                Log.e("PreferencesManager", "Invalid setting value: $settingName, defaulting to ADD_NOTE_DIALOG")
                OnLaunch.ADD_NOTE_DIALOG
            }
        }

    val themeTypeFlow: Flow<ThemeType> = context.dataStore.data
        .map { preferences ->
            val themeName = preferences[THEME_TYPE_KEY] ?: ThemeType.Default.name
            try {
                ThemeType.valueOf(themeName)
            } catch (e: IllegalArgumentException) {
                ThemeType.Default
            }
        }

    suspend fun setTopPageSetting(setting: OnLaunch) {
        Log.d("PreferencesManager", "Setting topPageSetting to: ${setting.name}")
        context.dataStore.edit { preferences ->
            preferences[UPON_LAUNCH] = setting.name
        }
    }

    suspend fun setThemeType(themeType: ThemeType) {
        Log.d("PreferencesManager", "Setting themeType to: ${themeType.name}")
        context.dataStore.edit { preferences ->
            preferences[THEME_TYPE_KEY] = themeType.name
        }
    }
}
