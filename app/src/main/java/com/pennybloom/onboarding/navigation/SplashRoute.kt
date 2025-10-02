package com.pennybloom.onboarding.navigation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pennybloom.onboarding.R
import kotlinx.coroutines.delay

enum class UserRole { GUARDIAN, CHILD }

@Composable
fun SplashRoute(
    onFinished: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f, animationSpec = tween(800))
    }

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("pennybloom.json"))
    val progress by animateLottieCompositionAsState(composition = composition, iterations = 1)

    LaunchedEffect(progress) {
        if (progress >= 1f) {
            delay(600)
        }
    }

    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .semantics { contentDescription = "Splash screen" },
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .graphicsLayer(alpha = alpha.value)
                        .semantics { contentDescription = "Blooming penny animation" }
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { onFinished(UserRole.GUARDIAN) }) {
                    Text(text = stringResource(id = R.string.guardian_cta))
                }
                Button(onClick = { onFinished(UserRole.CHILD) }) {
                    Text(text = stringResource(id = R.string.child_cta))
                }
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, description: String) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Text(text = description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
