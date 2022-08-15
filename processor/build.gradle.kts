plugins {
    kotlin("jvm")
}

group = "com.example"
version = "1.0-SNAPSHOT"


dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.0")
    implementation("com.squareup:kotlinpoet:1.10.2")
    implementation(project(":annotation"))
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
