import com.android.build.gradle.LibraryExtension
import com.skymonkey.convention.ExtensionType
import com.skymonkey.convention.configureBuildTypes
import com.skymonkey.convention.configureKotlinAndroid
import com.skymonkey.convention.configureKotlinJvm
import com.skymonkey.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class JvmKtorConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            dependencies {
                "implementation"(libs.findBundle("ktor").get())
            }
        }
    }
}