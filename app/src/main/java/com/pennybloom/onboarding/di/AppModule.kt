package com.pennybloom.onboarding.di

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.analytics.FirebaseAnalytics
import com.pennybloom.onboarding.analytics.AnalyticsLogger
import com.pennybloom.onboarding.analytics.FirebaseAnalyticsLogger
import com.pennybloom.onboarding.data.ChildProfileRepository
import com.pennybloom.onboarding.data.ChildProfileRepositoryImpl
import com.pennybloom.onboarding.data.ConsentDataStore
import com.pennybloom.onboarding.data.ConsentDataStoreImpl
import com.pennybloom.onboarding.data.GuardianAuthRepository
import com.pennybloom.onboarding.data.GuardianAuthRepositoryImpl
import com.pennybloom.onboarding.data.PennyBloomApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

private const val SHARED_PREFS_NAME = "pennybloom_guardian"

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindModule {
    @Binds
    abstract fun bindAnalyticsLogger(impl: FirebaseAnalyticsLogger): AnalyticsLogger

    @Binds
    abstract fun bindAuthRepository(impl: GuardianAuthRepositoryImpl): GuardianAuthRepository

    @Binds
    abstract fun bindChildProfileRepository(impl: ChildProfileRepositoryImpl): ChildProfileRepository

    @Binds
    abstract fun bindConsentStore(impl: ConsentDataStoreImpl): ConsentDataStore
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext app: Context): FirebaseAuth {
        FirebaseApp.initializeApp(app)
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext app: Context): FirebaseAnalytics = FirebaseAnalytics.getInstance(app)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideConsentDataStore(@ApplicationContext app: Context): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = CoroutineScope(SupervisorJob())) {
            app.preferencesDataStoreFile("consent")
        }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.pennybloom.dev/")
        .build()

    @Provides
    @Singleton
    fun providePennyBloomApi(retrofit: Retrofit): PennyBloomApi = retrofit.create(PennyBloomApi::class.java)
}
