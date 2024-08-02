plugins {
    alias(libs.plugins.runbuddy.android.library)
    alias(libs.plugins.runbuddy.android.room)
}

android {
    namespace = "com.skymonkey.core.database"
}

dependencies {
    implementation(libs.org.mongodb.bson)
    implementation(libs.bundles.koin)
    implementation(libs.androidx.paging.common)
    implementation(projects.core.domain)
}
