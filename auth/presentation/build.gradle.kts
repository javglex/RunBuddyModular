plugins {
    alias(libs.plugins.runbuddy.android.feature.ui)
}

android {
    namespace = "com.skymonkey.auth.presentation"
}

dependencies {
    //implementation(projects(":auth:domain")) // this is the old way to include other modules
    implementation(projects.auth.domain) // this is the new way. enabled by settings.gradle enableFeaturePreview()
    implementation(projects.core.domain)
}