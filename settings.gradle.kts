enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        maven {
            name = "groupezReleases"
            url = uri("https://repo.groupez.dev/releases")
        }
        maven {
            name = "groupezSnapshots"
            url = uri("https://repo.groupez.dev/snapshots")
        }
        gradlePluginPortal()
    }
}

rootProject.name = "zCrates"
include("api")

file("hooks").listFiles()?.forEach { file ->
    if (file.isDirectory and !file.name.equals("build")) {
        println("Include hooks:${file.name}")
        include(":hooks:${file.name}")
    }
}