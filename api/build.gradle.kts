rootProject.extra.properties["sha"]?.let { sha ->
    version = sha
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks {
    shadowJar {
        destinationDirectory.set(rootProject.extra["apiFolder"] as File)
    }

    build {
        dependsOn(shadowJar)
    }

    javadoc {
        options.encoding = "UTF-8"
        if (JavaVersion.current().isJava9Compatible)
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}