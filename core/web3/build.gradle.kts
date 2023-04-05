plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
}

kotlin {
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bignum)
                implementation(libs.atomicfu)
                implementation(libs.koin)
                implementation(libs.bundles.ktor)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.mockk)
                implementation(libs.ktor.mock)
                implementation(libs.bundles.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.bcprov)
                implementation(libs.ktor.okhttp)
            }
        }
        val androidUnitTest by getting
    }
}

android {
    namespace = "com.github.nailkhaf.web3"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}