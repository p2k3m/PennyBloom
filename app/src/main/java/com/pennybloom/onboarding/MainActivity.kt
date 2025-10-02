package com.pennybloom.onboarding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.pennybloom.onboarding.navigation.PennyBloomNavHost
import com.pennybloom.onboarding.ui.theme.PennyBloomTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PennyBloomAppRoot()
        }
    }
}

@Composable
fun PennyBloomAppRoot() {
    PennyBloomTheme {
        val navController = rememberNavController()
        val snackbarHostState = remember { SnackbarHostState() }
        Surface(modifier = Modifier.fillMaxSize()) {
            PennyBloomNavHost(
                navController = navController,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                snackbarHostState = snackbarHostState,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
