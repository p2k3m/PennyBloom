package com.pennybloom.onboarding.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import javax.inject.Inject
import javax.inject.Singleton

interface AnalyticsLogger {
    fun logScreenView(name: String)
    fun logAction(action: String, properties: Map<String, Any?> = emptyMap())
}

@Singleton
class FirebaseAnalyticsLogger @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsLogger {
    override fun logScreenView(name: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, name)
        }
    }

    override fun logAction(action: String, properties: Map<String, Any?>) {
        analytics.logEvent(action) {
            properties.forEach { (key, value) ->
                when (value) {
                    is String -> param(key, value)
                    is Double -> param(key, value)
                    is Int -> param(key, value.toLong())
                    is Long -> param(key, value)
                    is Float -> param(key, value.toDouble())
                    is Boolean -> param(key, if (value) "true" else "false")
                    else -> Unit
                }
            }
        }
    }
}
