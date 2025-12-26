plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinSerialization) // 应用序列化插件
    `maven-publish`
}

group = "com.hzz.miolib"
version = "1.0.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release", "debug")
    }

    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)

                // Ktor Common
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp) // Android 使用 OkHttp 引擎
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio) // Desktop 使用 CIO 引擎
            }
        }
    }
}

android {
    namespace = "com.hzz.miolib.smms"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}