import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.16"
}

group = "io.georgeous.piggyback"
version = "1.0-SNAPSHOT"

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION;
    
repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    
    build {
        doLast {
            copy {
                from(project.layout.buildDirectory.file("libs/${rootProject.name}-$version.jar"))
                into(rootProject.projectDir.resolve("server/plugins"))
                rename(".*", "${rootProject.name}.jar")
            }
        }
    }
    
    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to version)
        }
    }

    withType<JavaCompile> {
        val javaVersion = JavaVersion.toVersion(21)
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }
}