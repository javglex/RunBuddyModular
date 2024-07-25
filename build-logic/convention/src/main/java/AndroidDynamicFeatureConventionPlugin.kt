import com.android.build.api.dsl.DynamicFeatureExtension
import com.skymonkey.convention.ExtensionType
import com.skymonkey.convention.addUiLayerDependencies
import com.skymonkey.convention.configureAndroidCompose
import com.skymonkey.convention.configureBuildTypes
import com.skymonkey.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

// this plugin must be registered with build-logic:convention gradle to work
class AndroidDynamicFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.dynamic-feature")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<DynamicFeatureExtension> {
                configureKotlinAndroid(this)
                configureAndroidCompose(this)

                configureBuildTypes(this, ExtensionType.DYNAMIC_FEATURE)
            }

            dependencies {
                addUiLayerDependencies(target)
                "testImplementation"(kotlin("test"))
            }

            afterEvaluate {
                // Declare task dependency
                tasks.named("exportReleaseConsumerProguardFiles").configure {
                    dependsOn("extractProguardFiles")
                }
            }
        }
    }
}
