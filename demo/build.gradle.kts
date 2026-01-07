import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    // 应用插件
    alias(libs.plugins.buildConfig)
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
                implementation(libs.ktor.client.cio)
                implementation("org.slf4j:slf4j-simple:2.0.9")
                implementation(kotlin("reflect"))

                // R2 需要的 SDK
                implementation("aws.sdk.kotlin:s3:1.0.0")
            }
        }
    }
}

// 配置 BuildConfig 生成
buildConfig {
    // 生成类的包名
    packageName("com.miolib.demo")

    val localProperties = Properties()
    val localFile = project.rootProject.file("local.properties")
    if (localFile.exists()) {
        localProperties.load(localFile.inputStream())
    }
    val r2Endpoint = localProperties.getProperty("R2_ENDPOINT") ?: ""
    val r2AccessKey = localProperties.getProperty("R2_ACCESS_KEY") ?: ""
    val r2SecretKey = localProperties.getProperty("R2_SECRET_KEY") ?: ""
    val r2Bucket = localProperties.getProperty("R2_BUCKET_NAME") ?: ""

    buildConfigField("String", "R2_ENDPOINT", "\"$r2Endpoint\"")
    buildConfigField("String", "R2_ACCESS_KEY", "\"$r2AccessKey\"")
    buildConfigField("String", "R2_SECRET_KEY", "\"$r2SecretKey\"")
    buildConfigField("String", "R2_BUCKET_NAME", "\"$r2Bucket\"")
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