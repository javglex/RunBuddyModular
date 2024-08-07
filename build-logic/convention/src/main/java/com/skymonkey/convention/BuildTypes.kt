package com.skymonkey.convention

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DynamicFeatureExtension
import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureBuildTypes(
    commonExtension: CommonExtension<*, *, *, *, *>,
    extensionType: ExtensionType
) {
    commonExtension.run {
        buildFeatures {
            buildConfig = true
        }

        val apiKey = gradleLocalProperties(rootDir).getProperty("API_KEY")

        when (extensionType) {
            ExtensionType.APPLICATION -> {
                extensions.configure<ApplicationExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey)
                        }
                        create("staging") {
                            configureStagingBuildType(apiKey)
                        }
                    }
                }
            }
            ExtensionType.LIBRARY -> {
                extensions.configure<LibraryExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildType(commonExtension, apiKey)
                        }
                        create("staging") {
                            configureStagingBuildType(apiKey)
                        }
                    }
                }
            }

            ExtensionType.DYNAMIC_FEATURE -> {
                extensions.configure<DynamicFeatureExtension> {
                    buildTypes {
                        debug {
                            configureDebugBuildType(apiKey)
                        }
                        release {
                            configureReleaseBuildTypeDynamicFeature(commonExtension, apiKey)
                            isMinifyEnabled = false
                        }
                        create("staging") {
                            configureStagingBuildType(apiKey)
                        }
                    }
                }
            }
        }
    }
}

private fun BuildType.configureDebugBuildType(apiKey: String) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"http://192.168.1.11:8080\"")
}

private fun BuildType.configureStagingBuildType(apiKey: String) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runbuddy-45efafb71efb.herokuapp.com\"")
}

private fun BuildType.configureReleaseBuildType(
    commonExtension: CommonExtension<*, *, *, *, *>,
    apiKey: String
) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runbuddy-45efafb71efb.herokuapp.com\"")

    isMinifyEnabled = true // true enabled 8 to shrink apk to remove unecessary resources and obfuscate code.
    proguardFiles(
        commonExtension.getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}

private fun BuildType.configureReleaseBuildTypeDynamicFeature(
    commonExtension: CommonExtension<*, *, *, *, *>,
    apiKey: String
) {
    buildConfigField("String", "API_KEY", "\"$apiKey\"")
    buildConfigField("String", "BASE_URL", "\"https://runbuddy-45efafb71efb.herokuapp.com\"")

    isMinifyEnabled = true
    proguardFiles("proguard-rules.pro")
}
