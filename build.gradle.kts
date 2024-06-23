import io.papermc.paperweight.tasks.RemapJar
import io.papermc.paperweight.util.constants.OBF_NAMESPACE
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    application
    `maven-publish`

    kotlin("jvm") version "2.0.0" apply false
    id("idea")
    id("org.ajoberstar.grgit") version "4.1.1"
    id("io.github.goooler.shadow") version "8.1.7" // https://github.com/Goooler/shadow (fork of com.github.johnrengelman.shadow)
    id("xyz.jpenilla.run-paper") version "2.3.0" apply false
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
}

idea {
    project.jdkName = "21"
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "4.13.1-SNAPSHOT"

    apply(plugin = "java-library")
    apply(plugin = "kotlin")
    apply(plugin = "maven-publish")
    apply(plugin = "application")
    apply(plugin = "io.github.goooler.shadow")

    application {
        mainClass.set("net.dzikoysk.funnyguilds.FunnyGuilds")
    }

    repositories {
        /* Libs */
        mavenCentral()
        maven("https://maven.reposilite.com/releases")
        maven("https://maven.reposilite.com/jitpack")
        maven("https://storehouse.okaeri.eu/repository/maven-public")
        maven("https://repo.titanvale.net/releases")
        maven("https://repo.titanvale.net/snapshots")

        /* Servers */
        maven("https://libraries.minecraft.net")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")

        /* Hooks */
        maven("https://maven.enginehub.org/repo")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi")
        maven("https://nexus.codecrafter47.de/content/repositories/public")
        maven("https://repo.codemc.io/repository/maven-public")
        maven("https://repo.viaversion.com")
        maven("https://repo.mikeprimm.com")
    }
}

subprojects {
    dependencies {
        /* general */

        compileOnly("org.jetbrains:annotations:24.0.1")
        testImplementation(kotlin("stdlib-jdk8"))

        /* tests */

        val junit = "5.10.2"
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

        val mockito = "5.12.0"
        testImplementation("org.mockito:mockito-core:$mockito")
        testImplementation("org.mockito:mockito-junit-jupiter:$mockito")

        testImplementation(kotlin("test"))
        testImplementation("nl.jqno.equalsverifier:equalsverifier:3.14")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        options {
            (this as CoreJavadocOptions).addStringOption("Xdoclint:none", "-quiet") // mute warnings
        }
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
            freeCompilerArgs = listOf("-Xjvm-default=all") // Generate default methods in interfaces by default
        }
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
                from(components.getByName("java"))

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

    tasks.withType<Test> {
        jvmArgs("-XX:+EnableDynamicAgentLoading") // I hate JDK team (https://github.com/mockito/mockito/issues/3037)
        useJUnitPlatform()
        setForkEvery(1)
        maxParallelForks = 4

        testLogging {
            events(TestLogEvent.STARTED, TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true
            showStandardStreams = true
        }
    }
}


project(":nms").subprojects {
    dependencies {
        implementation("xyz.jpenilla:reflection-remapper:0.1.1")
    }

    val mcVersion = matchNmsMcVersion(name)
    if (mcVersion.minor < 17) {
        // Paperweight is only compatible with 1.17 and above
        return@subprojects
    }

    apply(plugin = "io.papermc.paperweight.userdev")

    val `is-1_20_5-or-newer` = mcVersion.minor >= 21 || mcVersion.minor == 20 && mcVersion.patch >= 5
    java {
        val javaVersion = when {
            `is-1_20_5-or-newer` -> JavaVersion.VERSION_21 // 1.20.5+ uses Java 21
            else -> JavaVersion.VERSION_17
        }

        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion

        withSourcesJar()
        withJavadocJar()
    }

    if (`is-1_20_5-or-newer`) {
        tasks.withType<RemapJar> {
            toNamespace = OBF_NAMESPACE
        }
    }
}

fun matchNmsMcVersion(projectName: String): MCVersion {
    val minorPatchPart = projectName.split("_").getOrNull(1) // v1_20R3 -> 20R3
    val minorPatchPartSplit = minorPatchPart?.split("R")
    val minorVersion = minorPatchPartSplit?.getOrNull(0)?.toIntOrNull() ?: 0 // 20R3 -> 20
    val patchVersion = minorPatchPartSplit?.getOrNull(1)?.toIntOrNull() ?: 0 // 20R3 -> 3

    return MCVersion(minorVersion, patchVersion)
}

data class MCVersion(val minor: Int, val patch: Int)