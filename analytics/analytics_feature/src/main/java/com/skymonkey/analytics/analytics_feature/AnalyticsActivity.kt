package com.skymonkey.analytics.analytics_feature

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.play.core.splitcompat.SplitCompat
import com.skymonkey.analytics.data.di.analyticsModule
import com.skymonkey.analytics.presentation.AnalyticsDashboardScreenRoot
import com.skymonkey.analytics.presentation.di.analyticsPresentationModule
import com.skymonkey.core.presentation.designsystem.RunBuddyTheme
import org.koin.core.context.loadKoinModules

/**
 * This activity will be launched by reflection
 */
class AnalyticsActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(
            listOf(
                analyticsModule,
                analyticsPresentationModule
            )
        )

        SplitCompat.installActivity(this) // from google.android.play.core. used to dynamically install modules

        setContent {
            RunBuddyTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "analytics_dashboard",
                ) {
                    composable("analytics_dashboard") {
                        AnalyticsDashboardScreenRoot(
                            onBackClick = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}