import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kobweb.application)
}

group = "com.forum.frontend"
version = "1.0-SNAPSHOT"

kobweb {
    app {
        index {
            description.set("Foro de la institución")
            head.add {
                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "stylesheet", href = "https://fonts.googleapis.com/css2?family=Outfit:wght@400;500;600;700&display=swap")
                link(rel = "stylesheet", href = "https://fonts.googleapis.com/css2?family=Material+Symbols+Rounded")
                link(rel = "stylesheet", href = "/styles.css")
            }
        }
    }
}

kotlin {
    configAsKobwebApplication("frontend")

    sourceSets {
        jsMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}