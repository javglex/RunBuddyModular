package com.skymonkey.runbuddy

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private lateinit var splitInstallManager: SplitInstallManager

    private val splitInstallListener =
        SplitInstallStateUpdatedListener { state ->
            when (state.status()) {
                SplitInstallSessionStatus.INSTALLED -> {
                    viewModel.setAnalyticsDialogVisibility(false)
                    Toast
                        .makeText(
                            applicationContext,
                            R.string.analytics_installed,
                            Toast.LENGTH_LONG
                        ).show()
                }
                SplitInstallSessionStatus.INSTALLING -> {
                    viewModel.setAnalyticsDialogVisibility(true)
                }
                SplitInstallSessionStatus.DOWNLOADING -> {
                    viewModel.setAnalyticsDialogVisibility(true)
                }
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    splitInstallManager.startConfirmationDialogForResult(state, this, 0)
                }
                SplitInstallSessionStatus.FAILED -> {
                    viewModel.setAnalyticsDialogVisibility(false)
                    Toast
                        .makeText(
                            applicationContext,
                            R.string.error_installation_failed,
                            Toast.LENGTH_LONG
                        ).show()
                }
            }
        }

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splitInstallManager = SplitInstallManagerFactory.create(this)

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                // while we are checking auth, keep showing splash screen
                viewModel.state.isCheckingAuth
            }
        }

        setContent {
            RunBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    if (!viewModel.state.isCheckingAuth) {
                        NavigationRoot(
                            navController = navController,
                            isLoggedIn = viewModel.state.isLoggedIn,
                            onAnalyticsClick = {
                                installOrStartAnalyticsFeature()
                            }
                        )

                        /*
                         * This can only be tested e2e by uploading this app to the play store
                         */
                        if (viewModel.state.showAnalyticsInstallDialog) {
                            Dialog(
                                onDismissRequest = {}
                            ) {
                                Column(
                                    modifier =
                                        Modifier
                                            .clip(RoundedCornerShape(15.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = stringResource(id = R.string.installing_module),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        splitInstallManager.registerListener(splitInstallListener)
    }

    override fun onPause() {
        super.onPause()
        splitInstallManager.unregisterListener(splitInstallListener)
    }

    private fun installOrStartAnalyticsFeature() {
        // if our analytics feature is installed..
        // launch or analytics activity using reflection
        if (splitInstallManager.installedModules.contains("analytics_feature")) {
            Intent()
                .setClassName(
                    packageName,
                    "com.skymonkey.analytics.analytics_feature.AnalyticsActivity"
                ).also(::startActivity)
            return
        }

        // if we don't have our analytics module, install it..
        val request =
            SplitInstallRequest
                .newBuilder()
                .addModule("analytics_feature")
                .build()

        splitInstallManager
            .startInstall(request)
            .addOnSuccessListener { }
            .addOnFailureListener {
                it.printStackTrace()
                Toast
                    .makeText(
                        applicationContext,
                        R.string.error_could_not_load_module,
                        Toast.LENGTH_LONG
                    ).show()
            }
    }
}
