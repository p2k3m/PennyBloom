package com.pennybloom.onboarding.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface ConsentDataStore {
    val consentFlow: Flow<Boolean>
    suspend fun setConsent(consent: Boolean)
}

@Singleton
class ConsentDataStoreImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ConsentDataStore {
    private val consentKey = booleanPreferencesKey("guardian_consent")

    override val consentFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[consentKey] ?: false
    }

    override suspend fun setConsent(consent: Boolean) {
        dataStore.edit { prefs ->
            prefs[consentKey] = consent
        }
    }
}
