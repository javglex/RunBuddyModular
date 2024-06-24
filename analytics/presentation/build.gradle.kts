plugins {
    alias(libs.plugins.runbuddy.android.feature.ui)
}

android {
    namespace = "com.skymonkey.analytics.presentation"
}

dependencies {
    implementation(projects.analytics.domain)
}
