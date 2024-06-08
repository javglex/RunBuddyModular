pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

// work around to gradle bug "Unable to make progress running work" when no test classes are available
gradle.startParameter.excludedTaskNames.addAll(listOf(":build-logic:convention:testClasses"))

rootProject.name = "RunBuddy"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS") // type-safe module access, when using implementation(project(":module"))
include(":app")
include(":auth:data")
include(":auth:domain")
include(":auth:presentation")
include(":core:presentation:designsystem")
include(":core:presentation:ui")
include(":core:domain")
include(":core:data")
include(":core:database")
include(":run:data")
include(":run:domain")
include(":run:presentation")
include(":run:location")
include(":run:network")
include(":analytics:data")
include(":analytics:domain")
include(":analytics:presentation")
include(":analytics:analytics_feature")
include(":wear:app")
include(":wear:run:data")
include(":wear:run:domain")
include(":wear:run:presentation")
include(":core:presentation:designsystem_wear")
include(":core:connectivity:domain")
include(":core:connectivity:data")
