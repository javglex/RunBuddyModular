import com.android.build.api.dsl.ApplicationExtension
import com.skymonkey.convention.ExtensionType
import com.skymonkey.convention.configureBuildTypes
import com.skymonkey.convention.configureKotlinAndroid
import com.skymonkey.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

// this plugin must be registered with build-logic:convention gradle to work
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                /*
                 * aliases must match versions in libs.versions.toml
                 */
                defaultConfig {
                    applicationId = libs.findVersion("projectApplicationId").get().toString()
                    targetSdk = libs
                        .findVersion("projectTargetSdkVersion")
                        .get()
                        .toString()
                        .toInt()

                    versionCode = libs
                        .findVersion("projectVersionCode")
                        .get()
                        .toString()
                        .toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }

                configureKotlinAndroid(this)
                configureBuildTypes(this, ExtensionType.APPLICATION)
            }
        }
    }
}
