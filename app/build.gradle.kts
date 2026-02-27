@file:OptIn(ExperimentalEncodingApi::class, ExperimentalBuildToolsApi::class,
    ExperimentalKotlinGradlePluginApi::class
)

import org.jetbrains.kotlin.buildtools.api.ExperimentalBuildToolsApi
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import kotlin.io.encoding.ExperimentalEncodingApi

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.mikepenz.aboutlibraries.plugin") version "13.2.1"
}

android {
    namespace = "cc.sovellus.vrcaa"
    compileSdk = 36

    defaultConfig {
        applicationId = "cc.sovellus.vrcaa"
        minSdk = 27
        targetSdk = 36
        versionCode = 200712
        versionName = "2.7.12"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GIT_HASH", "\"${getGitHash()}\"")
        buildConfigField("String", "GIT_BRANCH", "\"${getBranch()}\"")

        buildConfigField("String", "DISCORD_URL", "\"https://discord.gg/aJs8qJXuT3\"")
        buildConfigField("String", "CROWDIN_URL", "\"https://crowdin.com/project/vrcaa\"")
        buildConfigField("String", "KOFI_URL", "\"https://ko-fi.com/Nyabsi\"")
    }

    signingConfigs {
        create("release") {
            val storeFileEnv = System.getenv("SIGNING_STORE_FILE")
            val storePasswordEnv = System.getenv("SIGNING_STORE_PASSWORD")
            val keyAliasEnv = System.getenv("SIGNING_KEY_ALIAS")
            val keyPasswordEnv = System.getenv("SIGNING_KEY_PASSWORD")

            if (storeFileEnv != null && File(storeFileEnv).exists()) {
                storeFile = file(storeFileEnv)
                storePassword = storePasswordEnv
                keyAlias = keyAliasEnv
                keyPassword = keyPasswordEnv
            } else {
                // This warning can and should be ignored in local development environment, it's meant for CI
                println("Warning: Release signing configuration not fully set up from environment variables.")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin.jvmToolchain(21)

    buildFeatures {
        buildConfig = true
        compose = true
    }

    androidResources {
        generateLocaleConfig = true
    }

    flavorDimensions += "type"
    productFlavors {
        create("standard") {
            isDefault = true
            dimension = "type"
        }
        create("quest") {
            dimension = "type"
            applicationIdSuffix = ".quest"
        }
        create("pico") {
            dimension = "type"
            applicationIdSuffix = ".pico"
        }
        create("nightly") {
            dimension = "type"
            applicationIdSuffix = ".nightly"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.activity:activity-compose:1.12.4")
    implementation(platform("androidx.compose:compose-bom:2026.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose-android:2.10.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.squareup.okhttp3:okhttp:5.3.2")
    implementation("com.sealwu.jsontokotlin:library:3.7.4")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta08")
    implementation("com.github.bumptech.glide:glide:5.0.5")
    implementation("cafe.adriel.voyager:voyager-navigator:1.1.0-beta03")
    implementation("cafe.adriel.voyager:voyager-screenmodel:1.1.0-beta03")
    implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:1.1.0-beta03")
    implementation("cafe.adriel.voyager:voyager-tab-navigator:1.1.0-beta03")
    implementation("cafe.adriel.voyager:voyager-transitions:1.1.0-beta03")
    implementation("androidx.activity:activity-ktx:1.12.4")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.8")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.4.0")
    implementation("com.mikepenz:aboutlibraries-core:13.2.1")
    implementation("com.mikepenz:aboutlibraries-compose-m3-android:13.2.1")
    implementation ("androidx.glance:glance-appwidget:1.1.1")
    implementation ("androidx.glance:glance-material3:1.1.1@aar")
    implementation("net.thauvin.erik.urlencoder:urlencoder-lib:1.6.0")
    implementation("com.google.firebase:firebase-crashlytics:20.0.4")
    implementation("dev.turingcomplete:kotlin-onetimepassword:2.4.1")
    implementation("androidx.work:work-runtime:2.11.1")
}

// === Helpers ===

internal enum class GitHashType {
    GIT_HASH_COMMIT,
    GIT_HASH_BRANCH;
}

internal fun getGitHash(type: GitHashType): String? {
    try {
        val builder = ProcessBuilder("git", "rev-parse")
        when (type) {
            GitHashType.GIT_HASH_COMMIT -> {
                builder.command().add("--short")
            }
            GitHashType.GIT_HASH_BRANCH -> {
                builder.command().add("--abbrev-ref")
            }
        }
        builder.command().add("HEAD")
        builder.directory(project.rootDir)

        val process = builder.start()
        val inputStream = process.inputStream
        val errorStream = process.errorStream
        process.waitFor()

        return if (process.exitValue() == 0) {
            inputStream.bufferedReader()
                .use { it.readText().trim() }
        } else {
            val errorMessage = errorStream.bufferedReader().use { it.readText().trim() }
            logger.error("Error running git command: $errorMessage")
            null
        }
    } catch (_: Throwable) {
        return null
    }
}

fun getGitHash(): String =
    getGitHash(GitHashType.GIT_HASH_COMMIT) ?: "invalid-hash"

fun getBranch(): String =
    getGitHash(GitHashType.GIT_HASH_BRANCH) ?: "invalid-branch"

