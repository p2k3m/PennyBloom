package com.pennybloom.onboarding.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pennybloom.onboarding.analytics.AnalyticsLogger
import com.pennybloom.onboarding.auth.AuthViewModel
import com.pennybloom.onboarding.auth.GuardianSignupScreen
import com.pennybloom.onboarding.child.ChildProfileScreen
import com.pennybloom.onboarding.child.ChildProfileViewModel
import com.pennybloom.onboarding.util.rememberAnalyticsLogger
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PennyBloomNavHost(
    navController: NavHostController = rememberNavController(),
    snackbarHost: @Composable () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    analyticsLogger: AnalyticsLogger = rememberAnalyticsLogger()
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
        modifier = modifier.fillMaxSize()
    ) {
        composable(NavRoutes.Splash.route) {
            analyticsLogger.logScreenView("Splash")
            SplashRoute(
                onFinished = { role ->
                    when (role) {
                        UserRole.GUARDIAN -> navController.navigate(NavRoutes.GuardianSignup.route) {
                            popUpTo(NavRoutes.Splash.route) { inclusive = true }
                        }
                        UserRole.CHILD -> navController.navigate(NavRoutes.ChildHome.route) {
                            popUpTo(NavRoutes.Splash.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(NavRoutes.GuardianSignup.route) {
            analyticsLogger.logScreenView("GuardianSignup")
            val viewModel: AuthViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel.errorMessages) {
                viewModel.errorMessages.collectLatest { message ->
                    message?.let { snackbarHostState.showSnackbar(it) }
                }
            }

            GuardianSignupScreen(
                state = uiState,
                onPhoneChanged = viewModel::onPhoneChanged,
                onEmailChanged = viewModel::onEmailChanged,
                onRequestOtp = viewModel::requestOtp,
                onOtpChanged = viewModel::onOtpChanged,
                onVerifyOtp = {
                    viewModel.verifyOtp(onSuccess = {
                        navController.navigate(NavRoutes.ChildProfile.route) {
                            popUpTo(NavRoutes.GuardianSignup.route) { inclusive = true }
                        }
                    })
                },
                onUploadAadhaar = viewModel::onAadhaarSelected,
                snackbarHost = snackbarHost
            )
        }
        composable(NavRoutes.ChildProfile.route) {
            analyticsLogger.logScreenView("ChildProfile")
            val viewModel: ChildProfileViewModel = hiltViewModel()
            val uiState by viewModel.state.collectAsStateWithLifecycle()

            ChildProfileScreen(
                state = uiState,
                onNameChanged = viewModel::onNameChanged,
                onAgeChanged = viewModel::onAgeChanged,
                onConsentChanged = viewModel::onConsentChanged,
                onSaveProfile = {
                    viewModel.saveProfile(onSaved = { role ->
                        when (role) {
                            UserRole.GUARDIAN -> navController.navigate(NavRoutes.GuardianHome.route) {
                                popUpTo(NavRoutes.ChildProfile.route) { inclusive = true }
                            }
                            UserRole.CHILD -> navController.navigate(NavRoutes.ChildHome.route) {
                                popUpTo(NavRoutes.ChildProfile.route) { inclusive = true }
                            }
                        }
                    })
                },
                snackbarHost = snackbarHost
            )
        }
        composable(NavRoutes.GuardianHome.route) {
            analyticsLogger.logScreenView("GuardianHome")
            GuardianHomeScreen()
        }
        composable(NavRoutes.ChildHome.route) {
            analyticsLogger.logScreenView("ChildHome")
            ChildHomeScreen()
        }
    }
}

@Composable
private fun GuardianHomeScreen() {
    PlaceholderScreen(title = "Guardian Home", description = "Monitor your child's finances here.")
}

@Composable
private fun ChildHomeScreen() {
    PlaceholderScreen(title = "Child Home", description = "Track your allowance and goals.")
}
