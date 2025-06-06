import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.firebase.perf)

    alias(libs.plugins.compose.compiler)
}

android {
    composeCompiler {
        enableStrongSkippingMode = true
    }

    signingConfigs {
        create("release") {
            storeFile = file("${rootDir}/keystore/keystore.jks")
            storePassword = "HolyLumi"
            keyAlias = "key0"
            keyPassword = "HolyLumi"
        }
        applicationVariants.all { variant ->
            variant.outputs.all { output ->
                val appName = "ThemedManager"
                val versionName = defaultConfig.versionName
                output.outputFile.renameTo(
                    File(
                        output.outputFile.parent,
                        "${appName}-v${versionName}-${variant.buildType.name}.apk",
                    )
                )
            }
        }
    }

    namespace = "pro.themed.manager"
    compileSdk = 36

    defaultConfig {
        applicationId = "pro.themed.manager"
        minSdk = 26
        targetSdk = 36
        versionCode = SimpleDateFormat("yyMMdd").format(Date()).toInt()
        versionName = SimpleDateFormat("yy.MM.dd").format(Date()).toString()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("release")
            // isDebuggable = true
            // isJniDebuggable = true
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
            isJniDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
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
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.inappmessaging.display)
    implementation(libs.play.services.ads)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.rebugger)

    implementation(libs.ktsh)
    // Splash API
    implementation(libs.androidx.core.splashscreen)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    //  testImplementation(libs.androidx.runtime.tracing)

    // For AppWidgets support
    implementation(libs.androidx.glance.appwidget)

    // For interop APIs with Material 3
    implementation(libs.androidx.glance.material3)

    // For interop APIs with Material 2
    implementation(libs.androidx.glance.material)

    implementation("androidx.compose.material:material-icons-core:1.7.8")
}
