include(":xrouter")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        //mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
    }
}


include (":app")
include (":processor")
include (":annotation")

