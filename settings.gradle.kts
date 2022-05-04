pluginManagement {
    repositories {
        maven { setUrl("https://maven.minecraftforge.net") }
        maven { setUrl("https://maven.parchmentmc.org") }
        mavenCentral()
    }
}
rootProject.name = "synth-main"
includeBuild("../synth-build")

include(":synth-lib")
project(":synth-lib").projectDir = File("../synth-lib")