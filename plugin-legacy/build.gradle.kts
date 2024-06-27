plugins {
    id("net.minecrell.plugin-yml.bukkit")
}

tasks.withType<PublishToMavenRepository> {
    enabled = false
}

java {
    // Ah... sweet legacy never would leave us!
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile> {
    // Disables warning about Java 1.8 soon to be deprecated
    options.compilerArgs.add("-Xlint:-options")
}

@Suppress("VulnerableLibrariesLocal")
dependencies {
    shadow("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
}

val pluginName: String by rootProject.extra
val pluginPackageName: String by rootProject.extra
val pluginVersion: String by rootProject.extra
val pluginAuthor: String by rootProject.extra
val pluginWebsite: String by rootProject.extra

bukkit {
    name = pluginName
    main = "$pluginPackageName.legacy.FunnyGuildsLegacyPlugin"
    version = pluginVersion
    author = pluginAuthor
    website = pluginWebsite
}