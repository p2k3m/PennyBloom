package com.pennybloom.onboarding.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.pennybloom.onboarding.analytics.AnalyticsLogger
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@Composable
fun rememberAnalyticsLogger(): AnalyticsLogger {
    val context = LocalContext.current
    return remember {
        EntryPointAccessors.fromApplication(context.applicationContext, AnalyticsEntryPoint::class.java)
            .analyticsLogger()
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AnalyticsEntryPoint {
    fun analyticsLogger(): AnalyticsLogger
}
