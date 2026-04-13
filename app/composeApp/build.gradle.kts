import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.composeStabilityAnalyzer)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
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
            implementation(compose.materialIconsExtended)

            // Lifecycle
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            // Navigation 3
            implementation(libs.navigation3.ui)
            implementation(libs.lifecycle.viewmodel.navigation3)

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

            // Room (offline-first)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)

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
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "com.umain.omnismytho"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
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
// Reads api.baseUrl at execution time (not configuration time) so the task
// re-runs whenever the property changes, including after ngrok restarts.
val generateApiConfig by tasks.registering {
    val propsFile = rootProject.file("gradle.properties")
    val outputDir =
        layout.buildDirectory.dir("generated/apiconfig/com/umain/omnismytho/data/remote")

    inputs.file(propsFile)
    outputs.dir(outputDir)

    doLast {
        // Read fresh from file at execution time
        val props = Properties()
        propsFile.inputStream().use { props.load(it) }
        val baseUrl = props.getProperty("api.baseUrl", "http://10.0.2.2:8000/api/v1")

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
            """.trimMargin(),
        )
        println("ApiConfig.BASE_URL = $baseUrl")
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir(generateApiConfig.map { layout.buildDirectory.dir("generated/apiconfig") })
}

// ── Room KMP setup ──────────────────────────────────────────────────────────
room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Room KSP compiler for each target
    add("kspAndroid", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}
