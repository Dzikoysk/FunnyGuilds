import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("xyz.jpenilla.run-paper")
    kotlin("jvm")
}

publishing {
    repositories {
        maven {
            name = "reposilite"
            url = uri("https://maven.reposilite.com/${if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"}")
            credentials {
                username = System.getenv("MAVEN_NAME") ?: property("mavenUser").toString()
                password = System.getenv("MAVEN_TOKEN") ?: property("mavenPassword").toString()
            }
        }
    }
    publications {
        create<MavenPublication>("library") {
            from(components["shadow"])

            // Add external repositories to published artifacts
            // ~ btw: pls don't touch this
            pom.withXml {
                val repositories = asNode().appendNode("repositories")
                project.repositories.findAll(closureOf<Any> {
                    if (this is MavenArtifactRepository && this.url.toString().startsWith("https")) {
                        val repository = repositories.appendNode("repository")
                        repository.appendNode("id", this.url.toString().replace("https://", "").replace("/", "-").replace(".", "-").trim())
                        repository.appendNode("url", this.url.toString().trim())
                    }
                })
            }
        }
    }
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    /* funnyguilds */

    project(":nms").dependencyProject.subprojects.forEach {
        implementation(it)
    }
    implementation("net.dzikoysk:funnycommands:0.7.0")

    /* std */

    val expressible = "1.3.6"
    api("org.panda-lang:expressible:$expressible")
    testImplementation("org.panda-lang:expressible-junit:$expressible")

    /* okaeri config library */

    val okaeriConfigs = "5.0.5"
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigs")
    // okaeri holographicdisplays commons
    implementation("eu.okaeri:okaeri-commons-bukkit-holographicdisplays:0.2.27")

    /* messages libraries */

    val adventureVersion = "4.18.0"
    implementation("net.kyori:adventure-api:$adventureVersion")
    implementation("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    implementation("net.kyori:adventure-text-minimessage:$adventureVersion")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4") // adventure-platform has other versioning than adventure-api

    val yamlVersion = "6.8.0-SNAPSHOT"
    implementation("dev.peri.yetanothermessageslibrary:core:$yamlVersion")
    implementation("dev.peri.yetanothermessageslibrary:repository-okaeri:$yamlVersion")
    implementation("dev.peri.yetanothermessageslibrary:platform-bukkit:$yamlVersion")

    implementation("com.github.PikaMug:LocaleLib:4.1.0")

    /* general stuff */

    @Suppress("GradlePackageUpdate")
    implementation("com.zaxxer:HikariCP:4.0.3")

    @Suppress("GradlePackageUpdate")
    implementation("com.google.guava:guava:21.0") {
        because("WorldEdit defined a constraint that we must use 21.0 and there is no way to ignore it")
    }

    @Suppress("GradlePackageUpdate")
    implementation("com.google.code.gson:gson:2.8.0") {
        because("WorldEdit defined a constraint that we must use 2.8.0 and there is no way to ignore it")
    }

    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")

    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // probably fix for some exception?
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.20.0")

    // bukkit stuff
    shadow("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    shadow("org.apache.logging.log4j:log4j-core:2.20.0")

    /* hooks */

    shadow("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    shadow("net.milkbowl.vault:VaultAPI:1.7")
    shadow("me.clip:placeholderapi:2.11.6") {
//        because("PlaceholderAPI on versions higher than 2.10.9 causes GH-1700 for some unknown reason")
        exclude(group = "com.google.code.gson", module = "gson")
    }
    shadow("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    shadow("com.github.decentsoftware-eu:decentholograms:2.8.12")
    shadow("us.dynmap:DynmapCoreAPI:3.6")

    /* tests */
    testImplementation("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    testImplementation("com.mojang:authlib:3.2.38")
}

tasks.processResources {
    expand(
        "funnyGuildsVersion" to version,
        "funnyGuildsCommit" to grgit.head().abbreviatedId
    )
}

tasks.withType<ShadowJar> {
    archiveFileName.set("FunnyGuilds ${project.version}.${grgit.log().size} (MC 1.8-1.21).jar")
    mergeServiceFiles()

    relocate("net.dzikoysk.funnycommands", "net.dzikoysk.funnyguilds.libs.net.dzikoysk.funnycommands")
    relocate("panda.utilities", "net.dzikoysk.funnyguilds.libs.panda.utilities")
    relocate("javassist", "net.dzikoysk.funnyguilds.libs.javassist")
    relocate("com.zaxxer", "net.dzikoysk.funnyguilds.libs.com.zaxxer")
    relocate("com.google", "net.dzikoysk.funnyguilds.libs.com.google") {
        exclude("com.google.gson.**")
    }
    relocate("org.apache.commons.lang3", "net.dzikoysk.funnyguilds.libs.org.apache.commons.lang3")
    relocate("org.apache.logging", "net.dzikoysk.funnyguilds.libs.org.apache.logging")
    relocate("org.slf4j", "net.dzikoysk.funnyguilds.libs.org.slf4j")
    relocate("org.bstats", "net.dzikoysk.funnyguilds.libs.bstats")
    relocate("eu.okaeri", "net.dzikoysk.funnyguilds.libs.eu.okaeri")
    relocate("net.kyori", "net.dzikoysk.funnyguilds.libs.net.kyori")
    relocate("dev.peri", "net.dzikoysk.funnyguilds.libs.dev.peri")
    relocate("me.pikamug", "net.dzikoysk.funnyguilds.libs.me.pikamug")
    relocate("org.mariadb", "net.dzikoysk.funnyguilds.libs.org.mariadb")

    exclude("org/checkerframework/**")
    exclude("org/intellij/lang/annotations/**")
    exclude("org/jetbrains/annotations/**")
    exclude("META-INF/services/javax.annotation.processing.Processor")

    minimize {
        exclude(dependency("net.dzikoysk:funnycommands:.*"))
        exclude(dependency("com.fasterxml.jackson.core:jackson-core:.*"))
        exclude(dependency("org.mariadb.jdbc:mariadb-java-client:.*"))

        // nms implementation modules are not referenced in the project but are required at runtime
        parent!!.project(":nms").subprojects.forEach {
            exclude(project(it.path))
        }
    }
}

tasks {
    runServer {
        minecraftVersion("1.20.6")
    }
}
