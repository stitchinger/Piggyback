import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    id("java")
    id("io.papermc.paperweight.userdev") version "1.7.5"
}

group = "io.georgeous.piggyback"
version = "0.1"

paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.REOBF_PRODUCTION

repositories {
    mavenCentral()
}

dependencies {
    paperweight.paperDevBundle("1.21.3-R0.1-SNAPSHOT")
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
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    withType<JavaCompile> {
        val javaVersion = JavaVersion.toVersion(21)
        sourceCompatibility = javaVersion.toString()
        targetCompatibility = javaVersion.toString()
    }
}