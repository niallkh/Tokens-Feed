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
                implementation(project(":core:web3"))
                implementation(project(":core:database"))
                implementation(project(":core:datastore"))
                implementation(libs.sql.coroutines)
                implementation(libs.datastore)
                implementation(libs.bundles.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.sql.jvm)
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
    namespace = "com.github.nailkhaf.data.tokens"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}