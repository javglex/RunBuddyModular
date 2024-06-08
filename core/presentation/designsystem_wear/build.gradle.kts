plugins {
    alias(libs.plugins.runbuddy.android.library.compose)
}

android {
    namespace = "com.skymonkey.core.presentation.designsystem_wear"

    defaultConfig {
        minSdk = 30
    }
}

dependencies {
    api(projects.core.presentation.designsystem)

    implementation(libs.androidx.wear.compose.material)
}