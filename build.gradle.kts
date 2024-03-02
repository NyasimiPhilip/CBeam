// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.chaquo.python") version "15.0.1" apply false
    id("org.jetbrains.kotlin.kapt") version "2.0.0-Beta4"

}
buildscript {
    repositories {
        google()
        // Add other repositories if needed
    }
    dependencies {
        // Define plugin versions
        val nav_version = ("2.7.7")

        // Specify plugin dependencies
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        // Add other build script dependencies if needed
    }
}