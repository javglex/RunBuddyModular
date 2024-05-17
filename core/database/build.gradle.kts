plugins {
    alias(libs.plugins.runbuddy.android.library)
    alias(libs.plugins.runbuddy.android.room)
}

android {
    namespace = "com.skymonkey.core.database"
}

dependencies {
    implementation(libs.org.mongodb.bson)
    implementation(projects.core.domain)
}