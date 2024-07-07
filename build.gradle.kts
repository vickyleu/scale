plugins {
    id(libs.plugins.android.application.get().pluginId) apply false
    id(libs.plugins.android.library.get().pluginId) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    id(libs.plugins.kotlin.multiplatform.get().pluginId) apply false
    alias(libs.plugins.kotlinx.atomicfu) apply false
    alias(libs.plugins.dokka)
}

tasks.dokkaHtmlMultiModule {
    moduleName.set("Scale")
    moduleVersion.set(project.properties["versionName"]!!.toString())
    outputDirectory.set(file("$rootDir/doc/docs/reference"))
}