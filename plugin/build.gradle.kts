import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription
import kotlin.math.exp

plugins {
    id("net.minecrell.plugin-yml.paper")
    id("net.kyori.blossom") version "2.1.0"
    id("xyz.jpenilla.run-paper")
    kotlin("jvm")
}

val mcVersion: String by project
val pluginApiVersion: String by project
val paperVersion: String by project
val mcDataVersion: String by project
val runServerVersion: String by project

dependencies {
    /* funnyguilds */

    project(":nms").dependencyProject.subprojects.forEach {
        implementation(it)
    }
    implementation("net.dzikoysk:funnycommands:0.7.0") {
        exclude(group = "org.panda-lang.utilities", module = "di")
    }

    /* std */

    val expressibleGroup = "org.panda-lang"
    val expressible = "1.3.6"
    compileOnlyApi("${expressibleGroup}:expressible:$expressible")
    paperLibrary(expressibleGroup, "expressible", expressible)

    val diGroup = "org.panda-lang.utilities"
    val di = "1.8.0"
    compileOnlyApi("${diGroup}:di:$di")
    paperLibrary(diGroup, "di", di)

    /* okaeri config library */

    val okaeriConfigs = "5.0.2"
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:$okaeriConfigs")
    implementation("eu.okaeri:okaeri-configs-validator-okaeri:$okaeriConfigs")
    // okaeri holographicdisplays commons
    implementation("eu.okaeri:okaeri-commons-bukkit-holographicdisplays:0.2.25")

    /* messages libraries */
    val yamlVersion = "6.8.0-SNAPSHOT"
    implementation("dev.peri.yetanothermessageslibrary:core:$yamlVersion")
    implementation("dev.peri.yetanothermessageslibrary:repository-okaeri:$yamlVersion")
    implementation("dev.peri.yetanothermessageslibrary:platform-bukkit:$yamlVersion")

    implementation("com.github.PikaMug:LocaleLib:3.9")

    /* general stuff */

    paperLibrary("com.zaxxer:HikariCP:5.1.0")
    paperLibrary("org.mariadb.jdbc:mariadb-java-client:3.2.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // bukkit stuff
    shadow("io.papermc.paper:paper-api:${paperVersion}")

    /* hooks */

    shadow("com.sk89q.worldguard:worldguard-bukkit:7.0.9")
    shadow("net.milkbowl.vault:VaultAPI:1.7")
    shadow("me.clip:placeholderapi:2.11.6") {
        exclude(group = "com.google.code.gson", module = "gson")
    }
    shadow("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")
    shadow("com.github.decentsoftware-eu:decentholograms:2.8.8")
    shadow("us.dynmap:dynmap-api:3.0")

    /* tests */
    testImplementation("io.papermc.paper:paper-api:${paperVersion}")
    testRuntimeOnly("com.mojang:authlib:6.0.54")
    testRuntimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl:2.22.1")

    testImplementation("${expressibleGroup}:expressible-junit:$expressible")
    testRuntimeOnly("${expressibleGroup}:expressible:$expressible")
    testRuntimeOnly("${diGroup}:di:$di")
}

sourceSets {
    main {
        blossom {
            javaSources {
                property("mcDataVersion", mcDataVersion)
            }
        }
    }
}

val packageName = "net.dzikoysk.funnyguilds"
paper {
    name = rootProject.name
    main = "${packageName}.FunnyGuilds"
    version = "${project.version} Snowdrop-${grgit.head().abbreviatedId}"
    apiVersion = pluginApiVersion

    author = "FunnyGuilds Team"
    website = "https://github.com/FunnyGuilds"

    generateLibrariesJson = true
    loader = "${packageName}.FunnyGuildsLoader"

    serverDependencies {
        register("WorldEdit") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("WorldGuard") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Vault") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("DecentHolograms") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("HolographicDisplays") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Multiverse-Core") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("dynmap") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }

    permissions {
        register("funnyguilds.*") {
            default = BukkitPluginDescription.Permission.Default.OP
            childrenMap = mapOf(
                "funnyguilds.player" to true,
                "funnyguilds.vip" to true,
                "funnyguilds.admin" to true
            )
        }
        register("funnyguilds.admin") {
            default = BukkitPluginDescription.Permission.Default.OP
            childrenMap = mapOf(
                "funnyguilds.reload" to true,
                "funnyguilds.admin.build" to true,
                "funnyguilds.admin.interact" to true,
                "funnyguilds.admin.teleport" to true,
                "funnyguilds.admin.notification" to true,
                "funnyguilds.admin.disabledummy" to false,
                "funnyguilds.base.teleportTime.admin" to true
            )
        }
        register("funnyguilds.vip") {
            default = BukkitPluginDescription.Permission.Default.OP
            childrenMap = mapOf(
                "funnyguilds.vip.items" to true,
                "funnyguilds.vip.rank" to true,
                "funnyguilds.vip.base" to true,
                "funnyguilds.base.teleportTime.vip" to true
            )
        }
        register("funnyguilds.player") {
            default = BukkitPluginDescription.Permission.Default.TRUE
            childrenMap = mapOf(
                "funnyguilds.ally" to true,
                "funnyguilds.base" to true,
                "funnyguilds.break" to true,
                "funnyguilds.create" to true,
                "funnyguilds.delete" to true,
                "funnyguilds.deputy" to true,
                "funnyguilds.enlarge" to true,
                "funnyguilds.escape" to true,
                "funnyguilds.guild" to true,
                "funnyguilds.info" to true,
                "funnyguilds.invite" to true,
                "funnyguilds.items" to true,
                "funnyguilds.join" to true,
                "funnyguilds.kick" to true,
                "funnyguilds.leader" to true,
                "funnyguilds.leave" to true,
                "funnyguilds.playerinfo" to true,
                "funnyguilds.pvp" to true,
                "funnyguilds.ranking" to true,
                "funnyguilds.rankreset" to true,
                "funnyguilds.statsreset" to true,
                "funnyguilds.setbase" to true,
                "funnyguilds.tnt" to true,
                "funnyguilds.top" to true,
                "funnyguilds.validity" to true,
                "funnyguilds.war" to true,
                "funnyguilds.base.teleportTime.default" to true
            )
        }
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("FunnyGuilds ${project.version}.${grgit.log().size} (MC ${mcVersion}+).jar")
    mergeServiceFiles()

    setOf(
        "net.dzikoysk.funnycommands",
        "com.zaxxer",
        "org.bstats",
        "eu.okaeri",
        "dev.peri",
        "me.pikamug"
    ).forEach {
        relocate(it, "net.dzikoysk.funnyguilds.libs.$it")
    }

    exclude("kotlin/**")
    exclude("org/checkerframework/**")
    exclude("org/intellij/lang/annotations/**")
    exclude("org/jetbrains/annotations/**")
    exclude("javax/annotation/**")

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
        minecraftVersion(runServerVersion)
    }
}
