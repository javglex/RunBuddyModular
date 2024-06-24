plugins {
    alias(libs.plugins.runbuddy.android.application.wear.compose)
}

android {
    namespace = "com.skymonkey.wear.app"

    defaultConfig {
        minSdk =
            libs.versions.projectMinWearSdkVersion
                .get()
                .toInt()
    }
}

dependencies {
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.bundles.koin)

    implementation(projects.wear.run.presentation)
    implementation(projects.wear.run.data)

    implementation(projects.core.presentation.designsystemWear)
    implementation(projects.core.presentation.ui)
    implementation(projects.core.connectivity.domain)
    implementation(projects.core.connectivity.data)
}
