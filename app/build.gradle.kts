import com.android.build.gradle.ProguardFiles.getDefaultProguardFile
import org.gradle.internal.impldep.com.amazonaws.PredefinedClientConfigurations.defaultConfig
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    id("com.google.gms.google-services")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "ml.bublik.cz.firebasemltest"
        minSdkVersion(21)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), file("proguard-rules.pro"))
        }
    }

    lintOptions {
        lintConfig = file("lint-options.xml")
    }
    packagingOptions {
        exclude("META-INF/proguard/androidx-annotations.pro")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation("android.arch.core:runtime:1.1.1")
    implementation("android.arch.core:common:1.1.1")
    implementation(kotlin("stdlib-jdk7", KotlinCompilerVersion.VERSION))
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support:recyclerview-v7:28.0.0")
    implementation("com.android.support:design:28.0.0")
    implementation("com.android.support:support-v4:28.0.0")
    implementation("com.android.support.constraint:constraint-layout:1.1.3")
    implementation("com.google.firebase:firebase-core:16.0.6")
    implementation("com.google.firebase:firebase-ml-vision:18.0.2")
    implementation("com.google.firebase:firebase-firestore:17.1.4")
    implementation("org.jetbrains.anko:anko-commons:0.10.8")
    implementation("android.arch.lifecycle:extensions:1.1.1")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("com.android.support.test:runner:1.0.2")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:3.0.2")
}
