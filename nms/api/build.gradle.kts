val paperVersion: String by project
dependencies {
    paperweight.paperDevBundle(paperVersion)
    shadow("com.viaversion:viaversion-api:[4.0.0,5.0.0)")
}