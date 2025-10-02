package com.pennybloom.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnboardingFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun splashNavigatesToGuardianFlow() {
        composeTestRule.onNodeWithContentDescription("Splash screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("I'm a parent/guardian").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Guardian signup screen").assertIsDisplayed()
    }
}
