package com.pennybloom.onboarding.data

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

data class ChildProfile(
    val name: String,
    val age: Int,
    val consentGranted: Boolean
)

interface ChildProfileRepository {
    fun saveProfile(profile: ChildProfile)
    fun getProfile(): ChildProfile?
}

@Singleton
class ChildProfileRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : ChildProfileRepository {
    override fun saveProfile(profile: ChildProfile) {
        sharedPreferences.edit()
            .putString(KEY_NAME, profile.name)
            .putInt(KEY_AGE, profile.age)
            .putBoolean(KEY_CONSENT, profile.consentGranted)
            .apply()
    }

    override fun getProfile(): ChildProfile? {
        val name = sharedPreferences.getString(KEY_NAME, null) ?: return null
        val age = sharedPreferences.getInt(KEY_AGE, -1)
        if (age == -1) return null
        val consent = sharedPreferences.getBoolean(KEY_CONSENT, false)
        return ChildProfile(name, age, consent)
    }

    private companion object {
        const val KEY_NAME = "child_name"
        const val KEY_AGE = "child_age"
        const val KEY_CONSENT = "child_consent"
    }
}
