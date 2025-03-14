import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Domain"
            isStatic = true
        }
    }

    jvm("jvm") {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlin.datetime)
        }
        jvmTest.dependencies {
            implementation(libs.test.junit.jupiter.api)
            runtimeOnly(libs.test.junit.jupiter.engiene)
            implementation(libs.test.mockk)
            implementation(libs.test.strikt)
        }
    }
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}
