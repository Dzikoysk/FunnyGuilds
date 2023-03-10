import io.papermc.paperweight.tasks.RemapJar
import io.papermc.paperweight.util.constants.OBF_NAMESPACE

subprojects {
    apply(plugin = "io.papermc.paperweight.userdev")

    dependencies {
        implementation("xyz.jpenilla:reflection-remapper:0.1.1")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<RemapJar> {
        toNamespace = OBF_NAMESPACE
    }
}