buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.gradle)
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.google.firebase.firebase.perf) apply false
    id("com.ncorti.ktfmt.gradle") version("0.22.0") // Replace with latest version

}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    compilerOptions.freeCompilerArgs.addAll(
        "-P",
        "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true",
        "plugin:androidx.compose.compiler.plugins.kotlin:nonSkippingGroupOptimization=true"
    )
}

allprojects {
    apply(plugin = "com.ncorti.ktfmt.gradle")
    ktfmt { // 1
        kotlinLangStyle() // 2
    }
}