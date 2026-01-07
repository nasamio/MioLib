// demo/build.gradle.kts 完整代码
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.buildConfig)
    // 必须确保应用了序列化插件，否则 @Serializable 不生效
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":ui"))
                implementation(project(":smms"))
                implementation(project(":rss"))

                implementation(compose.material3)
                implementation(libs.androidx.navigation.compose)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")

                // --- 新增：解决 Unresolved reference 错误 ---
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.cio)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                // ---------------------------------------

                implementation("org.slf4j:slf4j-simple:2.0.9")
                implementation(kotlin("reflect"))
                implementation("aws.sdk.kotlin:s3:1.0.0")
            }
        }
    }
}

buildConfig {
    packageName("com.miolib.demo")
    val localProperties = Properties()
    val localFile = project.rootProject.file("local.properties")
    if (localFile.exists()) {
        localProperties.load(localFile.inputStream())
    }

    // R2 配置
    buildConfigField("String", "R2_ENDPOINT", "\"${localProperties.getProperty("R2_ENDPOINT") ?: ""}\"")
    buildConfigField("String", "R2_ACCESS_KEY", "\"${localProperties.getProperty("R2_ACCESS_KEY") ?: ""}\"")
    buildConfigField("String", "R2_SECRET_KEY", "\"${localProperties.getProperty("R2_SECRET_KEY") ?: ""}\"")
    buildConfigField("String", "R2_BUCKET_NAME", "\"${localProperties.getProperty("R2_BUCKET_NAME") ?: ""}\"")

    // Cloudflare KV 配置
    buildConfigField("String", "CF_ACCOUNT_ID", "\"${localProperties.getProperty("CF_ACCOUNT_ID") ?: ""}\"")
    buildConfigField("String", "CF_KV_NAMESPACE_ID", "\"${localProperties.getProperty("CF_KV_NAMESPACE_ID") ?: ""}\"")
    buildConfigField("String", "CF_API_TOKEN", "\"${localProperties.getProperty("CF_API_TOKEN") ?: ""}\"")
    val d1DatabaseId = localProperties.getProperty("CF_D1_DATABASE_ID") ?: ""
    buildConfigField("String", "CF_D1_DATABASE_ID", "\"$d1DatabaseId\"")
}

compose {
    desktop {
        application {
            mainClass = "com.miolib.demo.MainKt"
            nativeDistributions {
                targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
                packageName = "MioLibDemo"
                packageVersion = "1.0.0"
            }
        }
    }
}