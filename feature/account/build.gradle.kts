plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-parcelize")
}

kotlin {
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":data:tokens"))
                implementation(project(":core:web3"))
                implementation(libs.decompose)
                implementation(libs.bundles.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.mockk)
                implementation(libs.bundles.test)
            }
        }
        val androidMain by getting {
        }
        val androidUnitTest by getting
    }
}

android {
    namespace = "com.github.nailkhaf.feature.account"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}