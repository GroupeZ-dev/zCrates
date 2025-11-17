plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.0.0-beta11"
    id("re.alwyn974.groupez.repository") version "1.0.0"
}

apply("gradle/copy-build.gradle")

extra.set("targetFolder", file("target/"))
extra.set("apiFolder", file("target-api/"))
extra.set("classifier", System.getProperty("archive.classifier"))
extra.set("sha", System.getProperty("github.sha"))

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "re.alwyn974.groupez.repository")

    group = "fr.traqueur"
    version = "1.0.0"

    repositories {
        mavenCentral()

        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
        maven(url = "https://jitpack.io")
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

        compileOnly("net.kyori:adventure-platform-bukkit:4.3.4")
        compileOnly("org.mozilla:rhino:1.7.14")
        compileOnly("org.reflections:reflections:0.10.2")

        compileOnly(files(rootProject.files("libs/zMenu-1.1.0.4.jar")))

        /* Libraries */
        implementation("com.github.Traqueur-dev:Structura:1.5.0")
        implementation("com.github.Traqueur-dev.CommandsAPI:platform-spigot:4.2.3")
        implementation("fr.maxlego08.sarah:sarah:1.20.1")

        /* Test dependencies */
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
        testImplementation("org.mozilla:rhino:1.7.14")
        testImplementation("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
        testImplementation("org.slf4j:slf4j-simple:2.0.9")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.test {
        useJUnitPlatform()
    }

    tasks.shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveAppendix.set(if (project.path == ":") "" else project.name)
        archiveClassifier.set("")

        relocate("fr.traqueur.structura", "fr.traqueur.crates.libs.structura")
        relocate("fr.traqueur.commands", "fr.traqueur.crates.libs.commands")
        relocate("fr.maxlego08.sarah", "fr.traqueur.crates.libs.sarah")
    }

}

dependencies {
    api(project(":api"))
    api(project(":common"))
    rootProject.subprojects.filter { it.path.startsWith(":hooks:") }.forEach { subproject ->
        implementation(project(subproject.path))
    }
}

tasks {
    shadowJar {
        rootProject.extra.properties["sha"]?.let { sha ->
            archiveClassifier.set("${rootProject.extra.properties["classifier"]}-${sha}")
        } ?: run {
            archiveClassifier.set(rootProject.extra.properties["classifier"] as String?)
        }
        destinationDirectory.set(rootProject.extra["targetFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
        dependsOn(subprojects.map { it.tasks.shadowJar })
    }

    processResources {
        from("resources")
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}