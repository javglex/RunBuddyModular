plugins {
    alias(libs.plugins.runbuddy.android.library)
    alias(libs.plugins.runbuddy.jvm.ktor)
}

android {
    namespace = "com.skymonkey.run.network"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.data)
}