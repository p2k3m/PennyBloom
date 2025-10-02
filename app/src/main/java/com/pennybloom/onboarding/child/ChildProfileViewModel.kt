package com.pennybloom.onboarding.child

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pennybloom.onboarding.data.ChildProfile
import com.pennybloom.onboarding.data.ChildProfileRepository
import com.pennybloom.onboarding.data.ConsentDataStore
import com.pennybloom.onboarding.navigation.UserRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

sealed interface ProfileValidation {
    data object Valid : ProfileValidation
    data class Error(val message: String) : ProfileValidation
}

data class ChildProfileUiState(
    val childName: String = "",
    val age: Int = 10,
    val consentChecked: Boolean = false,
    val validation: ProfileValidation = ProfileValidation.Error("Guardian consent required"),
    val isSaving: Boolean = false
)

@HiltViewModel
class ChildProfileViewModel @Inject constructor(
    private val repository: ChildProfileRepository,
    private val consentDataStore: ConsentDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(ChildProfileUiState())
    val state: StateFlow<ChildProfileUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            consentDataStore.consentFlow.collect { consent ->
                _state.update { current ->
                    val validation = validate(current.childName, current.age, consent)
                    current.copy(consentChecked = consent, validation = validation)
                }
            }
        }
        repository.getProfile()?.let { profile ->
            _state.update {
                it.copy(
                    childName = profile.name,
                    age = profile.age,
                    consentChecked = profile.consentGranted,
                    validation = validate(profile.name, profile.age, profile.consentGranted)
                )
            }
        }
    }

    fun onNameChanged(name: String) {
        _state.update {
            val validation = validate(name, it.age, it.consentChecked)
            it.copy(childName = name, validation = validation)
        }
    }

    fun onAgeChanged(age: Int) {
        _state.update {
            val validation = validate(it.childName, age, it.consentChecked)
            it.copy(age = age, validation = validation)
        }
    }

    fun onConsentChanged(consent: Boolean) {
        viewModelScope.launch {
            consentDataStore.setConsent(consent)
        }
        _state.update {
            val validation = validate(it.childName, it.age, consent)
            it.copy(consentChecked = consent, validation = validation)
        }
    }

    fun saveProfile(onSaved: (UserRole) -> Unit) {
        val current = _state.value
        if (current.validation !is ProfileValidation.Valid) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            repository.saveProfile(ChildProfile(current.childName, current.age, current.consentChecked))
            _state.update { it.copy(isSaving = false) }
            onSaved(UserRole.GUARDIAN)
        }
    }

    private fun validate(name: String, age: Int, consent: Boolean): ProfileValidation {
        if (name.isBlank()) return ProfileValidation.Error("Name is required")
        if (age !in 10..17) return ProfileValidation.Error("Age must be between 10 and 17")
        if (!consent) return ProfileValidation.Error("Guardian consent required")
        return ProfileValidation.Valid
    }

    private inline fun <T> MutableStateFlow<T>.update(transform: (T) -> T) {
        value = transform(value)
    }
}
