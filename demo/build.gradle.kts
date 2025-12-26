plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvm("desktop")

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":ui"))
                implementation(project(":smms"))

                implementation(compose.material3)

                // Navigation
                implementation(libs.androidx.navigation.compose)

                // 协程
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")

                // --- 新增：运行时必要依赖 ---
                // 1. Ktor 引擎 (防止 ServiceLoader 找不到引擎导致 Crash)
                implementation(libs.ktor.client.cio)
                // 2. 日志实现 (防止 Ktor 报错 No SLF4J providers found)
                implementation("org.slf4j:slf4j-simple:2.0.9")
            }
        }
    }
}

compose {
    desktop {
        application {
            mainClass = "MainKt"
            nativeDistributions {
                targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi, org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb)
                packageName = "MioLibDemo"
                packageVersion = "1.0.0"
            }
        }
    }
}