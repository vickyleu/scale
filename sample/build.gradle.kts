plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.atomicfu)
}
kotlin{
    applyDefaultHierarchyTemplate()
    androidTarget()
    iosArm64()
    iosX64()
    iosSimulatorArm64()

    sourceSets{
        commonMain.get().dependencies {
            implementation(compose.ui)
            implementation(compose.uiUtil)
            implementation(compose.material3)
            implementation(project.dependencies.platform(libs.compose.bom))
            implementation(libs.kotlinx.datetime)
            implementation(projects.scaleImageViewerClassic)
            implementation(projects.scaleImageViewer)
            implementation(projects.scaleSamplingDecoder)
            implementation(projects.scaleZoomableView)
            implementation(libs.kotlinx.atomicfu)
        }
    }

}


kotlin {
    @Suppress("OPT_IN_USAGE")
    compilerOptions {
        freeCompilerArgs = listOf(
            "-Xexpect-actual-classes", // remove warnings for expect classes
            "-Xskip-prerelease-check",
            "-opt-in=kotlinx.cinterop.ExperimentalForeignApi",
            "-opt-in=org.jetbrains.compose.resources.InternalResourceApi",
        )
    }
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmTarget.get()))
    }
}


android {
    namespace = "com.jvziyaoyao.scale.sample"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.jvziyaoyao.scale.sample"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.1.0-alpha.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }

    buildFeatures {
        compose = true
    }

    packaging{
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    dependencies {
        implementation(compose.uiTooling)
        debugImplementation(libs.compose.ui.tooling.preview)
        implementation(libs.androidx.appcompat)
        implementation(libs.androidx.activity.compose)
        testImplementation(libs.junit.junit)
    }
}

//dependencies {
//    implementation(project(":scale-image-viewer-classic"))
//    implementation(project(":scale-image-viewer"))
//    implementation(project(":scale-sampling-decoder"))
////    implementation(libs.scale.image.viewer)
////    implementation(libs.scale.image.viewer.classic)
////    implementation(libs.scale.sampling.decoder)
//
//    implementation(libs.androidx.exif)
//
//    implementation(libs.androidx.navigation.compose)
//
//    implementation(libs.jvziyaoyao.origeek.ui)
//
//    implementation(libs.coil)
//    implementation(libs.coil.svg)
//    implementation(libs.coil.gif)
//    implementation(libs.coil.compose)
//
//    implementation(libs.google.accompanist.permissions)
//    implementation(libs.google.accompanist.systemuicontroller)
//
//    implementation(libs.androidx.constraintlayout.compose)
//
//    implementation(libs.core.ktx)
//    implementation(libs.appcompat)
//    implementation(libs.material)
//
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.material)
//    implementation(libs.androidx.compose.ui.tooling.preview)
//    debugImplementation(libs.androidx.compose.ui.tooling)
//    implementation(libs.androidx.compose.ui.util)
//    androidTestImplementation(libs.androidx.compose.ui.test)
//    implementation(libs.androidx.compose.material.icons.extended)
//
//    implementation(libs.androidx.lifecycle.runtime)
//    implementation(libs.androidx.activity.compose)
//    testImplementation(libs.junit.junit)
//    androidTestImplementation(libs.androidx.test.ext.junit)
//    androidTestImplementation(libs.espresso.core)
//}