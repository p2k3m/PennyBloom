package com.pennybloom.onboarding.child

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.pennybloom.onboarding.R
import kotlin.math.roundToInt

@Composable
fun ChildProfileScreen(
    state: ChildProfileUiState,
    onNameChanged: (String) -> Unit,
    onAgeChanged: (Int) -> Unit,
    onConsentChanged: (Boolean) -> Unit,
    onSaveProfile: () -> Unit,
    snackbarHost: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConsentDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .semantics { contentDescription = "Child profile screen" },
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = stringResource(id = R.string.child_profile_title),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        item {
            OutlinedTextField(
                value = state.childName,
                onValueChange = onNameChanged,
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Child name" },
                label = { Text(text = "Child's name") }
            )
        }
        item {
            Column {
                Text(text = "Age: ${'$'}{state.age}")
                RangeSlider(
                    values = 10f..state.age.toFloat(),
                    onValueChange = { range ->
                        val age = range.endInclusive.roundToInt().coerceIn(10, 17)
                        onAgeChanged(age)
                    },
                    valueRange = 10f..17f,
                    steps = 6,
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Select age" }
                )
            }
        }
        item {
            Column {
                RowWithCheckbox(
                    checked = state.consentChecked,
                    onCheckedChange = { checked ->
                        if (checked) {
                            showConsentDialog = true
                        } else {
                            onConsentChanged(false)
                        }
                    }
                )
                if (state.validation is ProfileValidation.Error) {
                    Text(
                        text = state.validation.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        item {
            Button(
                onClick = onSaveProfile,
                modifier = Modifier.fillMaxWidth(),
                enabled = state.validation is ProfileValidation.Valid && !state.isSaving
            ) {
                Text(text = if (state.isSaving) stringResource(id = R.string.saving) else stringResource(id = R.string.create_profile))
            }
        }
        item { snackbarHost() }
    }

    if (showConsentDialog) {
        AlertDialog(
            onDismissRequest = {
                showConsentDialog = false
            },
            confirmButton = {
                TextButton(onClick = {
                    onConsentChanged(true)
                    showConsentDialog = false
                }) {
                    Text(text = stringResource(id = R.string.consent_dialog_positive))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onConsentChanged(false)
                    showConsentDialog = false
                }) {
                    Text(text = stringResource(id = R.string.consent_dialog_negative))
                }
            },
            title = { Text(text = stringResource(id = R.string.consent_dialog_title)) },
            text = { Text(text = stringResource(id = R.string.consent_dialog_description)) }
        )
    }
}

@Composable
private fun RowWithCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.semantics { contentDescription = "Guardian consent" }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = stringResource(id = R.string.consent_dialog_title))
            Text(text = stringResource(id = R.string.consent_dialog_description))
        }
    }
}
