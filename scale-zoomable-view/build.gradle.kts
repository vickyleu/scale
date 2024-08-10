@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.atomicfu)
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin{
    applyDefaultHierarchyTemplate()
    androidTarget{
        publishLibraryVariants("release")
    }
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
            implementation(libs.kotlinx.atomicfu)
            api(libs.androidx.annotations)
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
    namespace = "com.jvziyaoyao.scale.zoomable"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    lint{
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
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

    publishing {
        singleVariant("release"){
            withJavadocJar()
            withSourcesJar()
        }
    }

    dependencies {
        implementation(compose.uiTooling)
        debugImplementation(libs.compose.ui.tooling.preview)
    }
}

