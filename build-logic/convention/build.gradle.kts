plugins {
    `kotlin-dsl`
}

group = "com.skymonkey.runbuddy.buildlogic"

dependencies {
    // we only need these plugins during compile time. they don't do anything during runtime.
    // thus we use compileOnly and not implementation
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "runbuddy.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidApplicationCompose") {
            id = "runbuddy.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
        register("androidApplicationWearCompose") {
            id = "runbuddy.android.application.wear.compose"
            implementationClass = "AndroidApplicationWearComposeConventionPlugin"
        }
        register("androidLibrary") {
            id = "runbuddy.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidLibraryCompose") {
            id = "runbuddy.android.library.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("androidFeatureUi") {
            id = "runbuddy.android.feature.ui"
            implementationClass = "AndroidFeatureUiConventionPlugin"
        }
        register("androidRoom") {
            id = "runbuddy.android.room"
            implementationClass = "AndroidRoomConventionPlugin"
        }
        register("androidDynamicFeature") {
            id = "runbuddy.android.dynamic.feature"
            implementationClass = "AndroidDynamicFeatureConventionPlugin"
        }
        register("jvmLibrary") {
            id = "runbuddy.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("jvmKtor") {
            id = "runbuddy.jvm.ktor"
            implementationClass = "JvmKtorConventionPlugin"
        }
    }
}