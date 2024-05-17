plugins {
    alias(libs.plugins.runbuddy.jvm.library)
}

dependencies {
    implementation(projects.core.domain)
}