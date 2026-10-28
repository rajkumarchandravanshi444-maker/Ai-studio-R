package com.example.shield.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shield.model.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "shield_ai_preferences")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("app_language")
        val AI_CONSENT = booleanPreferencesKey("ai_guardian_consent")
        val LAST_SCAN_TIMESTAMP = longPreferencesKey("last_scan_timestamp")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val selectedLanguage: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        val code = prefs[Keys.LANGUAGE] ?: "en"
        if (code == "hi") AppLanguage.HI else AppLanguage.EN
    }

    val aiConsentGranted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.AI_CONSENT] ?: false
    }

    val lastScanTimestamp: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_SCAN_TIMESTAMP] ?: 0L
    }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LANGUAGE] = language.code
        }
    }

    suspend fun setAiConsent(granted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.AI_CONSENT] = granted
        }
    }

    suspend fun updateLastScan(timestamp: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_SCAN_TIMESTAMP] = timestamp
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }
}
