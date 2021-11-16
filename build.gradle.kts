
plugins {
    kotlin("jvm") version "1.5.31" apply false
}


buildscript {

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:7.0.3")
        //classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath(kotlin("gradle-plugin", version = "1.5.31"))
//        classpath ("com.google.devtools.ksp:1.5.31")
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}