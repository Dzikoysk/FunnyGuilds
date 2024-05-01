import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    application
    `maven-publish`

    id("idea")
    id("org.ajoberstar.grgit") version "4.1.1"
    id("org.jetbrains.kotlin.jvm") version "1.9.22" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.3" apply false
    id("io.papermc.paperweight.userdev") version "1.5.12" apply false
}

idea {
    project.jdkName = "17"
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "4.12.1-SNAPSHOT"

    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "application")
    apply(plugin = "com.github.johnrengelman.shadow")

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

        val junit = "5.9.2"
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

        val mockito = "5.1.1"
        testImplementation("org.mockito:mockito-core:$mockito")
        testImplementation("org.mockito:mockito-junit-jupiter:$mockito")
        testImplementation("org.mockito:mockito-inline:$mockito")

        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.9.22")
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
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            languageVersion = "1.8"
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

    if (isEligibleForUserdev(name)) {
        apply(plugin = "io.papermc.paperweight.userdev")

        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17

            withSourcesJar()
            withJavadocJar()
        }
    }
}

fun isEligibleForUserdev(projectName: String): Boolean {
    val minorPatchPart = projectName.split("_").getOrNull(1) // v1_20R3 -> 20R3
    val minorVersion = minorPatchPart?.split("R")?.getOrNull(0)?.toIntOrNull() ?: 0 // 20R3 -> 20

    // paperweight only supports 1.17+
    return minorVersion >= 17
}