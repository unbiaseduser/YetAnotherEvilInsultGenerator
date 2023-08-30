import org.beryx.jlink.JlinkZipTask

plugins {
  id("java")
  id("application")
  kotlin("jvm")
  id("org.javamodularity.moduleplugin") version "1.8.12"
  id("org.openjfx.javafxplugin") version "0.0.13"
  id("org.beryx.jlink") version "2.25.0"
}

group = "com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop"
version = "1.0"

tasks.withType<JavaCompile>().configureEach {
  options.encoding = "UTF-8"
}

application {
  //mainModule.set("com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop")
  mainClass.set("com.sixtyninefourtwenty.yetanotherevilinsultgenerator.desktop.MyApplication")
}

kotlin {
  jvmToolchain(17)
}

javafx {
  version = "17.0.7"
  modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
  implementation(project(":shared"))
  implementation(libs.materialfx)
  implementation(libs.kotlinx.coroutines.javafx)
  testImplementation(libs.junit.jupiter.api)
  testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test>().configureEach {
  useJUnitPlatform()
}

jlink {
  imageZip.set(project.file("${buildDir}/distributions/app-${javafx.platform.classifier}.zip"))
  options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
  launcher {
    name = "app"
  }
}

tasks.withType<JlinkZipTask>().configureEach {
  group = "distribution"
}