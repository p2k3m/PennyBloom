package com.pennybloom.onboarding

import android.net.Uri
import com.pennybloom.onboarding.auth.AuthProgress
import com.pennybloom.onboarding.auth.AuthViewModel
import com.pennybloom.onboarding.data.GuardianAuthRepository
import com.pennybloom.onboarding.data.OtpResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val repository: GuardianAuthRepository = mockk(relaxed = true)
    private lateinit var viewModel: AuthViewModel
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        viewModel = AuthViewModel(repository)
    }

    @Test
    fun `requestOtp updates state on success`() = runTest(dispatcher) {
        coEvery { repository.sendOtp(any()) } returns OtpResult.Success

        viewModel.onPhoneChanged("9999999999")
        viewModel.requestOtp()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AuthProgress.OtpSent, state.progress)
        assertTrue(state.isOtpDialogVisible)
        coVerify { repository.sendOtp("9999999999") }
    }

    @Test
    fun `verifyOtp emits error on failure`() = runTest(dispatcher) {
        coEvery { repository.sendOtp(any()) } returns OtpResult.Success
        coEvery { repository.verifyOtp(any()) } returns OtpResult.Error("Invalid OTP")

        viewModel.onPhoneChanged("9999999999")
        viewModel.requestOtp()
        advanceUntilIdle()

        viewModel.onOtpChanged("1234")
        viewModel.verifyOtp {}
        advanceUntilIdle()

        assertEquals(AuthProgress.OtpSent, viewModel.uiState.value.progress)
    }

    @Test
    fun `aadhaar selection caches uri`() {
        val uri = Uri.parse("content://test/aadhaar.pdf")
        viewModel.onAadhaarSelected(uri)
        assertEquals(uri, viewModel.uiState.value.aadhaarUri)
    }
}
