import me.modmuss50.mpp.ReleaseType

plugins {
  id("xyz.jpenilla.quiet-architectury-loom") version "1.7-SNAPSHOT"
  id("me.modmuss50.mod-publish-plugin") version "0.6.2"
}

loom.forge.mixinConfig("spawnChunkRadius.mixins.json")

dependencies {
  minecraft("com.mojang:minecraft:1.20.1")
  mappings(loom.officialMojangMappings())
  forge("net.minecraftforge:forge:1.20.1-47.3.0")
}

val Project.releaseNotes: Provider<String>
  get() = providers.environmentVariable("RELEASE_NOTES")

publishMods.modrinth {
  projectId = "HiQ6jypW"
  type = ReleaseType.STABLE
  file = tasks.remapJar.flatMap { it.archiveFile }
  changelog = releaseNotes
  accessToken = providers.environmentVariable("MODRINTH_TOKEN")
  minecraftVersions = listOf("1.20.1")
  modLoaders = listOf("forge")
}

tasks.processResources {
  val props = mapOf(
    "version" to project.version,
    "github_url" to rootProject.providers.gradleProperty("githubUrl").get(),
    "description" to project.description,
  )
  inputs.properties(props)
  filesMatching("META-INF/mods.toml") {
    // filter manually to avoid trying to replace $Initializer in initializer class name...
    filter { string ->
      var result = string
      for ((key, value) in props) {
        result = result.replace("\${$key}", value.toString())
      }
      result
    }
  }
}

tasks.compileJava {
  options.release = 17
}
