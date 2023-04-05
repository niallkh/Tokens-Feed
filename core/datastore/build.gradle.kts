plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.wire")
}

kotlin {
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.datastore)
                implementation(libs.wire)
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
            dependencies {
                implementation(libs.sql.android)
            }
        }
        val androidUnitTest by getting
    }
}

android {
    namespace = "com.github.nailkhaf.datastore"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}

wire {
    kotlin {
    }
}
