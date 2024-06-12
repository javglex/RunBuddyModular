plugins {
    alias(libs.plugins.runbuddy.android.library)
}

android {
    namespace = "com.skymonkey.wear.run.data"

    defaultConfig {
        minSdk = libs.versions.projectMinWearSdkVersion.get().toInt()
    }
}

dependencies {
    implementation(libs.androidx.health.services.client)
    implementation(libs.bundles.koin)
    implementation(projects.wear.run.domain)
    implementation(projects.core.domain)

    implementation(projects.core.connectivity.domain)

}