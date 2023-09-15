@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.sixtyninefourtwenty.yetanotherevilinsultgenerator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sixtyninefourtwenty.yetanotherevilinsultgenerator"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        setProperty("archivesBaseName", "Yet Another Evil Insult Generator v$versionName")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {

    implementation(project(":shared"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.preference.ktx)
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)
    implementation(libs.work.runtime.ktx)
    implementation(libs.multistateview)
    implementation(libs.flowlayout)
    implementation(libs.bottomsheetalertdialog)
    implementation(libs.materialspinner)
    implementation(libs.customactionmode)
    implementation(libs.basefragments)
    implementation(libs.theming)
    implementation(libs.custompreferences)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}