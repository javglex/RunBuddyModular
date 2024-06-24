plugins {
    alias(libs.plugins.runbuddy.android.dynamic.feature)
}
android {
    namespace = "com.skymonkey.analytics.analytics_feature"
}

dependencies {
    // only dynamic features are allowed to depend on our m ain app
    implementation(project(":app"))
    implementation(libs.androidx.navigation.compose)
    api(projects.analytics.presentation)
    implementation(projects.analytics.domain)
    implementation(projects.analytics.data)
    implementation(projects.core.database)
}
