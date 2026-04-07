import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            // Compose
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.material.icons.extended)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation
            implementation(libs.navigation.compose)

            // Ktor (HTTP)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Koin (DI)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Coil3 (Images)
            implementation(libs.coil3.compose)
            implementation(libs.coil3.network.ktor)

            // Revolver (MVI)
            implementation(libs.revolver)

            // Serialization
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

            // Logging
            implementation(libs.napier)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.umain.omnismytho"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.umain.omnismytho"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

// ── Generate ApiConfig.kt from gradle.properties ────────────────────────────
val generateApiConfig by tasks.registering {
    val baseUrl = providers.gradleProperty("api.baseUrl")
        .getOrElse("http://10.0.2.2:8000/api/v1")
    val outputDir = layout.buildDirectory.dir("generated/apiconfig/com/umain/omnismytho/data/remote")

    outputs.dir(outputDir)

    doLast {
        val dir = outputDir.get().asFile
        dir.mkdirs()
        dir.resolve("ApiConfig.kt").writeText(
            """
            |package com.umain.omnismytho.data.remote
            |
            |/**
            | * Auto-generated from gradle.properties — do not edit manually.
            | * Updated by api/run_api.sh when ngrok starts.
            | */
            |object ApiConfig {
            |    const val BASE_URL = "$baseUrl"
            |}
            """.trimMargin()
        )
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir(generateApiConfig.map { layout.buildDirectory.dir("generated/apiconfig") })
}
