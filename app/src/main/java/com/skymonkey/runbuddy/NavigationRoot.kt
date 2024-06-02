package com.skymonkey.runbuddy

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import com.skymonkey.auth.presentation.intro.IntroScreenRoot
import com.skymonkey.auth.presentation.login.LoginScreenRoot
import com.skymonkey.auth.presentation.register.RegisterScreenRoot
import com.skymonkey.run.presentation.active_run.ActiveRunScreenRoot
import com.skymonkey.run.presentation.active_run.service.ActiveRunService
import com.skymonkey.run.presentation.run_overview.RunOverviewScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if(isLoggedIn) "run" else "auth"
    ) {
        authGraph(navController)
        runGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "intro",
        route = "auth"
    ) {
        composable(route = "intro") {
            IntroScreenRoot(
                onSignUpClick = {
                    navController.navigate("register")
                },
                onSignInClick = {
                    navController.navigate("login")
                }
            )
        }
        composable(route = "register") {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate("login") {
                        popUpTo("register") {
                            inclusive = true // pop the register screen as well
                            saveState = true
                        }
                        restoreState = true // restore login screen state
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate("login")
                }
            )
        }
        composable(route = "login") {
            LoginScreenRoot(
                onLoginSuccess = {
                    navController.navigate("run") {
                        popUpTo("auth") { // pop everything from the auth graph
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate("register") {
                        popUpTo("login") {
                            inclusive = true // pop the login screen as well
                            saveState = true
                        }
                        restoreState = true // restore register screen state
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(navController: NavHostController) {
    navigation(
        startDestination = "run_overview",
        route = "run"
    ) {
        composable("run_overview") {
            RunOverviewScreenRoot(
                onStartRunClick = {
                    navController.navigate("active_run")
                }
            )
        }
        composable(
            route = "active_run",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = ActiveRunService.DEEPLINK_URI
                }
            )
        ) {
            val context = LocalContext.current
            ActiveRunScreenRoot(
                onNavigateBack = {
                     navController.navigateUp()
                },
                onFinishRun = {
                    navController.navigateUp()
                },
                onServiceToggle = { shouldServiceRun ->
                    if(shouldServiceRun) {
                        context.startService(ActiveRunService.createStartIntent(context, MainActivity::class.java))
                    } else {
                        context.startService(ActiveRunService.createStopIntent(context))
                    }

                }
            )
        }
    }
}