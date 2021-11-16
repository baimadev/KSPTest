
plugins {
    id ("com.android.application")
    id ("kotlin-android")
    id ("com.google.devtools.ksp") version ("1.5.31-1.0.0")
}

android {
    compileSdk=31

    defaultConfig {
        applicationId = "com.holderzone.store.ksptest"
        minSdk = 21
        targetSdk = 31
        versionCode= 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        val release = getByName("release")
        release.apply {

            sourceSets {
                getByName("main") {
                    java.srcDir(File("build/generated/ksp/release/kotlin")) // 指定ksp生成目录，否则编译器不会之别生成的代码
                }
            }

            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        val debug = getByName("debug")
        debug.apply {

            sourceSets {
                getByName("main") {
                    java.srcDir(File("build/generated/ksp/debug/kotlin")) // 指定ksp生成目录，否则编译器不会之别生成的代码
                }
            }
        }

    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")

    testImplementation ("junit:junit:4.+")

    androidTestImplementation ("androidx.test.ext:junit:1.1.2")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.3.0")

    ksp(project(":processor"))
    implementation(project(":processor"))
}