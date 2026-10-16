plugins {
    id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT"
}

version = property("version")!!
base { archivesName.set("ad-astra-per-aspera-${property("minecraft_version")}") }

repositories {
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}

dependencies {
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")
    implementation("com.teamresourceful.resourcefullib:resourcefullib-fabric-26.2:${property("resourcefullib_version")}")
    implementation("com.teamresourceful.resourcefulconfig:resourcefulconfig-fabric-26.2:${property("resourcefulconfig_version")}")
    include("com.teamresourceful.resourcefullib:resourcefullib-fabric-26.2:${property("resourcefullib_version")}")
    include("com.teamresourceful.resourcefulconfig:resourcefulconfig-fabric-26.2:${property("resourcefulconfig_version")}")
    implementation("teamreborn:energy:5.0.0")
    include("teamreborn:energy:5.0.0")
    implementation("javazoom:jlayer:1.0.1")
    include("javazoom:jlayer:1.0.1")
    compileOnly("com.teamresourceful:bytecodecs:1.1.3")
    compileOnly("org.jetbrains:annotations:26.1.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
    options.compilerArgs.addAll(listOf("-Xmaxerrs", "5000"))
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("fabric.mod.json") { expand("version" to project.version) }
}
