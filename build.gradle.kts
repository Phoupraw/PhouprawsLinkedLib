plugins {
    kotlin("jvm")
    id("fabric-loom")
    `maven-publish`
}
version = property("mod_version")!!
group = property("maven_group")!!
val minecraftVersion = property("minecraft_version")!!
val baseName = property("archives_base_name").toString()

repositories {
    mavenLocal {
        content {
            includeGroup("phoupraw.mcmod")
        }
    }
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content {
            includeGroup("maven.modrinth")
        }
    }
    maven("https://minecraft.curseforge.com/api/maven/") {
        name = "CurseForge"
        content {
            includeGroup("curse.maven")
        }
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
        content {
            includeGroup("dev.isxander.yacl") //yet-another-config-lib-fabric
        }
    }
    maven("https://maven.quiltmc.org/repository/release") {
        content {
            includeGroup("org.quiltmc.parsers")
        }
    }
    maven("https://oss.sonatype.org/content/repositories/snapshots") {
        content {
            includeGroupAndSubgroups("com.twelvemonkeys")
        }
    }
    maven("https://maven.terraformersmc.com/") {
        name = "TerraformersMC"
        content {
            includeGroup("com.terraformersmc") //modmenu
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modCompileOnly("net.fabricmc:fabric-loader:${property("loader_version")}")
    modApi("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}") { exclude(module = "fabric-loader") }
    modCompileOnly(modLocalRuntime("dev.isxander.yacl:yet-another-config-lib-fabric:${property("yet_another_config_lib")}")!!)
    //    modLocalRuntime("com.terraformersmc:modmenu:${property("modmenu")}")
    //compileOnlyApi(annotationProcessor("org.projectlombok:lombok:${property("lombok")}")!!)
    include(modApi("teamreborn:energy:${property("energy_api")}") {
        isTransitive = false
    })
    //由于KT自带的与java互操作不太好用，所以我自己写了一个
    modCompileOnly("phoupraw.mcmod:FabricAPIKotlinOverwrite:+")
    modCompileOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}") { exclude(module = "fabric-transfer-api-v1") }
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
}

tasks {
    processResources {
        inputs.property("version", version)
        filesMatching("fabric.mod.json") {
            expand("version" to version)
        }
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
        kotlinOptions.freeCompilerArgs += "-Xextended-compiler-checks"
        kotlinOptions.freeCompilerArgs += "-Xlambdas=indy"
        kotlinOptions.freeCompilerArgs += "-Xjdk-release=17"
    }
    compileJava {
        targetCompatibility = "17"
    }
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
java {
    //    withJavadocJar()
    withSourcesJar()
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = baseName
            version = version
            println(getComponents().toList())
            from(getComponents()["java"])
        }
    }
    //    repositories {
    //        repositories.mavenLocal()
    //    }
}
fabricApi {
    configureDataGeneration()
}
kotlin {
    jvmToolchain(17)
}