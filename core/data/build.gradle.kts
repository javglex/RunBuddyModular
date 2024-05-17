plugins {
    alias(libs.plugins.runbuddy.android.library)
    alias(libs.plugins.runbuddy.jvm.ktor)
}

android {
    namespace = "com.skymonkey.core.data"
}

dependencies {

    implementation(libs.timber)

    implementation(projects.core.domain)
    implementation(projects.core.database)
}