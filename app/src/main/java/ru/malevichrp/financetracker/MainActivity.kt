package ru.malevichrp.financetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation3.runtime.NavKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.malevichrp.auth.AuthRoute
import ru.malevichrp.auth.AuthScreen
import ru.malevichrp.auth.AuthViewModel
import ru.malevichrp.designsystem.ui.theme.FinanceTrackerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinanceTrackerTheme {
                FinanceApp()
            }
        }
    }
}

@Composable
fun FinanceApp() {
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            FinanceNavHost({ message: String ->
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }, navController)
        }
    }
}

@Composable
fun FinanceNavHost(snackBarMessage: (String) -> Unit, navController: NavHostController) {
    NavHost(
        navController,
        startDestination = AuthRoute
    ) {
        composable<AuthRoute> {
            AuthScreen(
                viewModel = hiltViewModel<AuthViewModel>(),
                onSuccessLogin = {
                    navController.navigate(HomeRoute) {
                        popUpTo<AuthRoute> { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = { navController.navigate(HomeRoute) },
                showErrorMessage = snackBarMessage
            )
        }
        composable<HomeRoute> {
            Box(Modifier.fillMaxSize()) {
                Text("Not implemented", Modifier.align(Alignment.Center))
            }
        }
    }
}

@Serializable
object HomeRoute : NavKey