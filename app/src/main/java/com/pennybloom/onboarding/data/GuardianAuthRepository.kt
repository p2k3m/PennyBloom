package com.pennybloom.onboarding.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

sealed interface OtpResult {
    data object Success : OtpResult
    data class Error(val message: String) : OtpResult
}

interface GuardianAuthRepository {
    suspend fun sendOtp(phoneOrEmail: String): OtpResult
    suspend fun verifyOtp(code: String): OtpResult
    fun cacheAadhaar(uri: Uri?)
    fun getCachedAadhaar(): Uri?
}

@Singleton
class GuardianAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : GuardianAuthRepository {

    private var aadhaarUri: Uri? = null
    private var verificationId: String? = null

    override suspend fun sendOtp(phoneOrEmail: String): OtpResult {
        return try {
            firebaseAuth.signInAnonymously().await()
            verificationId = "mockVerificationId"
            OtpResult.Success
        } catch (ex: Exception) {
            OtpResult.Error(ex.localizedMessage ?: "Unable to send OTP")
        }
    }

    override suspend fun verifyOtp(code: String): OtpResult {
        return if (verificationId != null && code.length >= 4) {
            OtpResult.Success
        } else {
            OtpResult.Error("Invalid OTP")
        }
    }

    override fun cacheAadhaar(uri: Uri?) {
        aadhaarUri = uri
    }

    override fun getCachedAadhaar(): Uri? = aadhaarUri
}
