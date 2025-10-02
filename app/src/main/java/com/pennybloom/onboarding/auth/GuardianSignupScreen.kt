package com.pennybloom.onboarding.auth

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pennybloom.onboarding.R

@Composable
fun GuardianSignupScreen(
    state: GuardianAuthUiState,
    onPhoneChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onRequestOtp: () -> Unit,
    onOtpChanged: (String) -> Unit,
    onVerifyOtp: () -> Unit,
    onUploadAadhaar: (Uri?) -> Unit,
    snackbarHost: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val uploadLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onUploadAadhaar(uri)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .semantics { contentDescription = "Guardian signup screen" },
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = stringResource(id = R.string.verify_identity),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Start
            )
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = onPhoneChanged,
                label = { Text(text = "Phone number") },
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Enter phone" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
            OutlinedTextField(
                value = state.email,
                onValueChange = onEmailChanged,
                label = { Text(text = "Email (optional)") },
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Enter email" },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Button(
                onClick = onRequestOtp,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.progress !is AuthProgress.Loading
            ) {
                Text(text = if (state.progress is AuthProgress.Loading) "Sending OTP..." else "Send OTP")
            }
            Button(
                onClick = { uploadLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(id = R.string.aadhaar_upload))
            }
            state.aadhaarUri?.let {
                Text(
                    text = "Selected: ${'$'}{it.lastPathSegment}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.semantics { contentDescription = "Aadhaar file selected" }
                )
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            snackbarHost()
            Text(
                text = "We use OTP verification and Aadhaar proof to keep PennyBloom safe for families.",
                fontSize = 14.sp
            )
        }
    }

    if (state.isOtpDialogVisible) {
        OtpDialog(
            otpCode = state.otpCode,
            onOtpChanged = onOtpChanged,
            onDismiss = {},
            onVerifyOtp = onVerifyOtp,
            isLoading = state.progress is AuthProgress.Loading
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OtpDialog(
    otpCode: String,
    onOtpChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onVerifyOtp: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onVerifyOtp, enabled = otpCode.length >= 4 && !isLoading) {
                Text(text = if (isLoading) "Verifying..." else stringResource(id = R.string.otp_verify))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.retry))
            }
        },
        title = { Text(text = stringResource(id = R.string.otp_sent)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "Enter the 6-digit code sent to your phone/email")
                OutlinedTextField(
                    value = otpCode,
                    onValueChange = onOtpChanged,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Enter OTP" }
                )
            }
        }
    )
}
