plugins {
    alias(libs.plugins.runbuddy.android.library.compose)
}

android {
    namespace = "com.skymonkey.core.presentation.designsystem_wear"

    defaultConfig {
        minSdk =
            libs.versions.projectMinWearSdkVersion
                .get()
                .toInt()
    }
}

dependencies {
    api(projects.core.presentation.designsystem)

    implementation(libs.androidx.wear.compose.material)
}
