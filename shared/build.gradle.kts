plugins {
    kotlin("multiplatform")
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.realm)
}

group = "com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared"
version = "1.0"

kotlin {
    androidTarget()
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                api(libs.ktor.client.core)
                api(libs.kotlinx.datetime)
                api(libs.realm)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        //Custom source set holding stuff sharable between Android and JVM
        val androidAndJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                api(libs.ktor.client.okhttp)
            }
        }
        val androidMain by getting {
            dependsOn(getByName("androidAndJvmMain"))
        }
        val androidUnitTest by getting
        val jvmMain by getting {
            dependsOn(getByName("androidAndJvmMain"))
        }
        val jvmTest by getting
    }
}

android {
    namespace = "com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared"
    compileSdk = 33
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    dependencies {
        implementation(libs.appcompat)
        implementation(libs.preference.ktx)
        coreLibraryDesugaring(libs.desugar.jdk.libs)
    }
}
