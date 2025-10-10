import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    kotlin("plugin.serialization") version "2.1.0"

}

android {
    composeCompiler {
        enableStrongSkippingMode = true
    }
    namespace = "pro.themed.audhdlauncher"
    compileSdk = 36

    signingConfigs {
        create("release") {
            storeFile = file("${rootDir}/keystore/keystore.jks")
            storePassword = "HolyLumi"
            keyAlias = "key0"
            keyPassword = "HolyLumi"
        }
    }

    defaultConfig {
        applicationId = "pro.themed.audhdlauncher"
        minSdk = 23
        targetSdk = 36
        versionCode = SimpleDateFormat("yyMMdd").format(Date()).toInt()
        versionName = SimpleDateFormat("yy.MM.dd").format(Date()).toString()
        signingConfig = signingConfigs.getByName("release")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug { signingConfig = signingConfigs.getByName("release") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.rebugger)

    implementation (libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Koin for Android
    implementation("io.insert-koin:koin-android:4.1.0")
    // Koin for Jetpack Compose (if you're using Compose)
    implementation("io.insert-koin:koin-androidx-compose:4.1.0")

}
