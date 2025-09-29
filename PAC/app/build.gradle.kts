val ktor_version: String by project

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    id("org.jetbrains.kotlinx.kover") version "0.9.2"

}

android {
    namespace = "com.example.foodly"
    compileSdk = 34               // rimane 34

    defaultConfig {
        applicationId = "com.example.foodly"
        minSdk = 26
        targetSdk = 34            // rimane 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
    packaging.resources {
        excludes += listOf(
            "/META-INF/{AL2.0,LGPL2.1}",
            "META-INF/INDEX.LIST",
            "META-INF/io.netty.versions.properties"
        )
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // ——— Navigation Compose (downgrade a 2.8.0 per compileSdk 34) ———
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // ——— Lifecycle Compose (versioni compatibili con API 34) ———
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.0")

    // Ktor server & client
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.logback.classic.v1211)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.content.negotiation)
    implementation("io.ktor:ktor-client-core:2.x.x") {
        exclude(group = "ch.qos.logback", module = "logback-classic")
    }
    // Coil per immagini
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation(libs.androidx.material.icons.extended.android)
    implementation(libs.material3)

    // Vico per chart
    implementation(libs.core)
    implementation(libs.compose)
    implementation(libs.compose.m3)

    testImplementation(libs.junit)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

// Configurazione Kover per reportistiche di copertura del codice
kover {
    reports {
        // Report totali per tutto il progetto
        total {
            xml {
                onCheck = true // Genera report XML quando si eseguono i test
            }
            html {
                onCheck = true // Genera report HTML quando si eseguono i test
            }
            verify {
                onCheck = true // Verifica la copertura quando si eseguono i test
                rule {
                    // Regola minima di copertura del codice
                    minBound(80) // Almeno 80% di copertura
                }
            }
            
            // Filtra le classi da includere/escludere nei report
            filters {
                excludes {
                    // Escludi file generati automaticamente
                    classes("*.BuildConfig*")
                    classes("*.R")
                    classes("*.R$*")
                    classes("*.databinding.*")
                    classes("*.generated.*")
                    // Escludi Compose preview e funzioni di preview
                    classes("*ComposableSingletons*")
                    classes("*Preview*")
                    classes("*Kt")
                }
            }
        }
    }
}