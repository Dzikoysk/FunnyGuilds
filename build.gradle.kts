import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    application
    `maven-publish`

    kotlin("jvm") version "2.0.0" apply false
    id("idea")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
    id("org.ajoberstar.grgit") version "4.1.1"
    id("io.github.goooler.shadow") version "8.1.7" // https://github.com/Goooler/shadow (fork of com.github.johnrengelman.shadow)
    id("net.minecrell.plugin-yml.paper") version "0.6.0" apply false
    id("io.papermc.paperweight.userdev") version "1.7.1" apply false
    id("net.kyori.blossom") version "2.1.0" apply false
    id("xyz.jpenilla.run-paper") version "2.3.0" apply false
}

idea {
    project.jdkName = "21"
}

allprojects {
    group = "net.dzikoysk.funnyguilds"
    version = "5.0.0-SNAPSHOT"

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
        maven("https://repo.papermc.io/repository/maven-public/")

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
        testImplementation(kotlin("stdlib"))

        /* tests */

        val junit = "5.10.2"
        testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")

        val mockito = "5.12.0"
        testImplementation("org.mockito:mockito-core:$mockito")
        testImplementation("org.mockito:mockito-junit-jupiter:$mockito")

        testImplementation(kotlin("test"))
        testImplementation("nl.jqno.equalsverifier:equalsverifier:3.14.3")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Javadoc> {
        (options as StandardJavadocDocletOptions).let {
            it.addStringOption("Xdoclint:none", "-quiet") // mute warnings
            it.links(
                "https://jd.papermc.io/paper/1.20.6/",
                "https://javadoc.io/doc/org.panda-lang/expressible/1.3.6/",
            )
            it.encoding = "UTF-8"
        }
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            freeCompilerArgs = listOf("-Xjvm-default=all") // Generate default methods in interfaces by default
        }
    }

    publishing {
        repositories {
            maven {
                name = "reposilite"
                url = uri(
                    "https://maven.reposilite.com/${
                        if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"
                    }"
                )
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
                            repository.appendNode(
                                "id",
                                this.url.toString().replace("https://", "").replace("/", "-").replace(".", "-").trim()
                            )
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
