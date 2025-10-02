package com.pennybloom.onboarding.navigation

sealed class NavRoutes(val route: String) {
    data object Splash : NavRoutes("splash")
    data object GuardianSignup : NavRoutes("guardian_signup")
    data object ChildProfile : NavRoutes("child_profile")
    data object GuardianHome : NavRoutes("guardian_home")
    data object ChildHome : NavRoutes("child_home")
}
