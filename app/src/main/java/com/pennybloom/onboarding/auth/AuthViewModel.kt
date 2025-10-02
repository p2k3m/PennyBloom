package com.pennybloom.onboarding.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennybloom.onboarding.data.GuardianAuthRepository
import com.pennybloom.onboarding.data.OtpResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthProgress {
    data object Idle : AuthProgress
    data object Loading : AuthProgress
    data object OtpSent : AuthProgress
    data object Verified : AuthProgress
}

data class GuardianAuthUiState(
    val phoneNumber: String = "",
    val email: String = "",
    val otpCode: String = "",
    val aadhaarUri: Uri? = null,
    val progress: AuthProgress = AuthProgress.Idle,
    val isOtpDialogVisible: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: GuardianAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GuardianAuthUiState())
    val uiState: StateFlow<GuardianAuthUiState> = _uiState.asStateFlow()

    private val _errorMessages = MutableSharedFlow<String?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val errorMessages: SharedFlow<String?> = _errorMessages.asSharedFlow()

    fun onPhoneChanged(value: String) {
        _uiState.update { it.copy(phoneNumber = value) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onOtpChanged(value: String) {
        _uiState.update { it.copy(otpCode = value.take(6)) }
    }

    fun onAadhaarSelected(uri: Uri?) {
        if (uri == null) {
            emitError("No file selected")
            return
        }
        val isValid = uri.toString().endsWith(".pdf", ignoreCase = true) ||
            uri.toString().endsWith(".jpg", ignoreCase = true) ||
            uri.toString().endsWith(".jpeg", ignoreCase = true)
        if (isValid) {
            repository.cacheAadhaar(uri)
            _uiState.update { it.copy(aadhaarUri = uri) }
        } else {
            emitError("Please upload a PDF or JPG of the Aadhaar card")
        }
    }

    fun requestOtp() {
        val contact = _uiState.value.phoneNumber.ifBlank { _uiState.value.email }
        if (contact.isBlank()) {
            emitError("Please enter phone or email")
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(progress = AuthProgress.Loading) }
            when (val result = repository.sendOtp(contact)) {
                OtpResult.Success -> {
                    _uiState.update { it.copy(progress = AuthProgress.OtpSent, isOtpDialogVisible = true) }
                }
                is OtpResult.Error -> {
                    _uiState.update { it.copy(progress = AuthProgress.Idle) }
                    emitError(result.message)
                }
            }
        }
    }

    fun verifyOtp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(progress = AuthProgress.Loading) }
            when (val result = repository.verifyOtp(_uiState.value.otpCode)) {
                OtpResult.Success -> {
                    _uiState.update { it.copy(progress = AuthProgress.Verified, isOtpDialogVisible = false) }
                    onSuccess()
                }
                is OtpResult.Error -> {
                    _uiState.update { it.copy(progress = AuthProgress.OtpSent) }
                    emitError(result.message)
                }
            }
        }
    }

    private fun emitError(message: String?) {
        viewModelScope.launch {
            _errorMessages.emit(message)
        }
    }

    private inline fun <T> MutableStateFlow<T>.update(transform: (T) -> T) {
        value = transform(value)
    }
}
