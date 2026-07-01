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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation3.runtime.NavKey
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import ru.malevichrp.auth.AuthRoute
import ru.malevichrp.auth.AuthScreen
import ru.malevichrp.auth.AuthViewModel
import ru.malevichrp.datastore.TokenDataSource
import ru.malevichrp.designsystem.ui.theme.FinanceTrackerTheme
import ru.malevichrp.registration.RegisterRoute
import ru.malevichrp.registration.RegistrationScreen
import ru.malevichrp.registration.RegistrationViewModel

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
                onRegisterClick = { navController.navigate(RegisterRoute) },
                showErrorMessage = snackBarMessage
            )
        }
        composable<RegisterRoute> {
            RegistrationScreen(
                viewModel = hiltViewModel<RegistrationViewModel>(),
                onSuccessRegister = { navController.popBackStack() },
                onBackClick = { navController.popBackStack() },
                showMessage = snackBarMessage
            )
        }
        composable<HomeRoute> {
            Box(Modifier.fillMaxSize()) {
                TokensDebugScreen()
            }
        }
    }
}

@Composable
fun TokensDebugScreen(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current.applicationContext

    val tokenDataSource = remember(context) {
        EntryPointAccessors.fromApplication(
            context,
            TokenDataSourceEntryPoint::class.java,
        ).tokenDataSource()
    }

    val tokens by tokenDataSource.tokens.collectAsStateWithLifecycle(
        initialValue = null,
    )

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (tokens == null) {
            Text("Tokens: null")
        } else {
            Text(
                text = """
                    Access token:
                    ${tokens?.accessToken}

                    Refresh token:
                    ${tokens?.refreshToken}
                """.trimIndent(),
            )
        }
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface TokenDataSourceEntryPoint {
    fun tokenDataSource(): TokenDataSource
}

@Serializable
object HomeRoute : NavKey