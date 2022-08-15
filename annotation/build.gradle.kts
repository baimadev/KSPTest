
plugins {
    kotlin("jvm")
}

group = "com.example"
version = "1.0-SNAPSHOT"


dependencies {
    implementation(kotlin("stdlib"))
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
