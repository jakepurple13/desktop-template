import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    // __KOTLIN_COMPOSE_VERSION__
    kotlin("jvm") version "1.4.20"
    // __LATEST_COMPOSE_RELEASE_VERSION__
    id("org.jetbrains.compose") version (System.getenv("COMPOSE_TEMPLATE_COMPOSE_VERSION") ?: "0.2.0-build128")
}

repositories {
    jcenter()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    //compileOnly(group = "uk.co.caprica", name = "vlcj", version = "5.0.0")
    //implementation("uk.co.caprica:vlcj:4.7.0")
    //implementation("tk.ivybits.javi:java:+")
    compileOnly(group = "javax.media", name = "jmf", version = "2.1.1e")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinJvmComposeDesktopApplication"
        }
    }
}
