plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.mikepenz.aboutlibraries.plugin") version "10.10.0"
}

android {
    namespace = "cc.sovellus.vrcaa"
    compileSdk = 35

    defaultConfig {
        applicationId = "cc.sovellus.vrcaa"
        minSdk = 27
        targetSdk = 35
        versionCode = 200105
        versionName = "2.1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "GIT_HASH", "\"${getGitHash()}\"")
        buildConfigField("String", "GIT_BRANCH", "\"${getBranch()}\"")
        buildConfigField("String", "DISCORD_URL", "\"https://discord.gg/aJs8qJXuT3\"")
        buildConfigField("String", "CROWDIN_URL", "\"https://crowdin.com/project/vrcaa\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
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
        }
    }
}

// credit: https://github.com/amwatson/CitraVR/blob/master/src/android/app/build.gradle.kts#L255C1-L275C2
fun getGitHash(): String =
    runGitCommand(ProcessBuilder("git", "rev-parse", "--short", "HEAD")) ?: "invalid-hash"

fun getBranch(): String =
    runGitCommand(ProcessBuilder("git", "rev-parse", "--abbrev-ref", "HEAD")) ?: "invalid-branch"

fun runGitCommand(command: ProcessBuilder) : String? {
    try {
        command.directory(project.rootDir)
        val process = command.start()
        val inputStream = process.inputStream
        val errorStream = process.errorStream
        process.waitFor()

        return if (process.exitValue() == 0) {
            inputStream.bufferedReader()
                .use { it.readText().trim() } // return the value of gitHash
        } else {
            val errorMessage = errorStream.bufferedReader().use { it.readText().trim() }
            logger.error("Error running git command: $errorMessage")
            return null
        }
    } catch (e: Exception) {
        logger.error("$e: Cannot find git")
        return null
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3-android:1.3.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose-android:2.8.7")
    implementation("androidx.appcompat:appcompat:1.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("ru.gildor.coroutines:kotlin-coroutines-okhttp:1.0")
    implementation("com.sealwu.jsontokotlin:library:3.7.4")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("cafe.adriel.voyager:voyager-navigator:1.1.0-beta02")
    implementation("cafe.adriel.voyager:voyager-screenmodel:1.1.0-beta02")
    implementation("cafe.adriel.voyager:voyager-bottom-sheet-navigator:1.1.0-beta02")
    implementation("cafe.adriel.voyager:voyager-tab-navigator:1.1.0-beta02")
    implementation("cafe.adriel.voyager:voyager-transitions:1.1.0-beta02")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.33.2-alpha")
    implementation("androidx.compose.material:material-icons-extended:1.7.5")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
    implementation("com.mikepenz:aboutlibraries-core:10.10.0")
    implementation("com.mikepenz:aboutlibraries-compose-m3-android:10.10.0@aar")
    implementation ("androidx.glance:glance-appwidget:1.1.1")
    implementation ("androidx.glance:glance-material3:1.1.1@aar")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0")
}
