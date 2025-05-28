plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        val navVersion = "2.8.9"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}
