import com.android.build.gradle.LibraryExtension
import com.skymonkey.convention.ExtensionType
import com.skymonkey.convention.addUiLayerDependencies
import com.skymonkey.convention.configureAndroidCompose
import com.skymonkey.convention.configureBuildTypes
import com.skymonkey.convention.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.kotlin

class AndroidFeatureUiConventionPlugin: Plugin<Project> {
    override fun apply(target: Project) {
        target.run {
            pluginManager.run {
                apply("runbuddy.android.library.compose")
            }

            dependencies {
                addUiLayerDependencies(target)
            }
        }
    }

}