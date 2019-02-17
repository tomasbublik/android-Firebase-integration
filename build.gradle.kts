buildscript {
    val kotlinVersion by rootProject.extra { "1.3.11" }

    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.3.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:google-services:4.2.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks {
    val cleanBuildDir by registering(Delete::class) {}
    delete(buildDir)
}