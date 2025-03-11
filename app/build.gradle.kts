import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.build.config)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

buildConfig {
    buildConfigField("webPaScheme", "http")
    buildConfigField("webPaHostname", "130.162.226.194")
    buildConfigField("webPaAuthorization", "dXNlcjpwYXNz")
    buildConfigField("webPaDevicesPort", 6200)
    buildConfigField("webPaDeviceDataPort", 6100)
}

kotlin {
    androidTarget().compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    jvm("desktop")

    sourceSets {
        val desktopMain by getting
        val desktopTest by getting

        commonMain.dependencies {
            implementation(projects.domain)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlin.serialization.json)
            implementation(libs.kotlin.datetime)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)

            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.navigation)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.json)
            implementation(libs.ktor.client.xml)

            implementation(libs.cryptography.core)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)

            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.androidx.splashscreen)
            implementation(libs.androidx.room.runtime.android)

            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.cryptography.provider.jdk)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)

            implementation(libs.cryptography.provider.apple)
        }
        desktopMain.dependencies {
            implementation(libs.kotlinx.coroutines.swing)

            implementation(compose.desktop.currentOs)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.cryptography.provider.jdk)
        }
        desktopTest.dependencies {
            implementation(libs.test.junit.jupiter.api)
            runtimeOnly(libs.test.junit.jupiter.engiene)
            implementation(libs.test.mockk)
            implementation(libs.test.strikt)
        }
    }
}

android {
    namespace = "com.globallogic.rdkb.remotemanagement"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.globallogic.rdkb.remotemanagement"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    signingConfigs.create("staging") {
        storeFile(rootProject.file("./signingKey/stagingKey"))
        storePassword = "remotemanagement"
        keyPassword = "remotemanagement"
        keyAlias = "staging"
    }
    packaging.resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
    buildTypes.getByName("debug") {
        signingConfig = signingConfigs.getByName("staging")
        isMinifyEnabled = false
    }
    buildTypes.create("staging") {
        signingConfig = signingConfigs.getByName("staging")
        isMinifyEnabled = false
        isDebuggable = false
    }
    buildTypes.getByName("release") {
        isMinifyEnabled = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

composeCompiler {
    featureFlags.set(setOf(ComposeFeatureFlag.StrongSkipping, ComposeFeatureFlag.OptimizeNonSkippingGroups))
}

compose.desktop.application {
    mainClass = "com.globallogic.rdkb.remotemanagement.MainKt"

    nativeDistributions {
        targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
        packageName = "com.globallogic.rdkb.remotemanagement"
        packageVersion = "1.0.0"
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid",libs.androidx.room.compiler)
    add("kspIosSimulatorArm64",libs.androidx.room.compiler)
    add("kspIosX64",libs.androidx.room.compiler)
    add("kspIosArm64",libs.androidx.room.compiler)
    add("kspDesktop", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata" ) {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

tasks.named<Test>("desktopTest") {
    useJUnitPlatform()
}
