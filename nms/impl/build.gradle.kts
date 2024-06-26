val paperVersion: String by project
dependencies {
    implementation(project(":nms:api"))
    paperweight.paperDevBundle(paperVersion)
}