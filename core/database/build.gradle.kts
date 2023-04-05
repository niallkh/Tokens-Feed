plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization")
    id("com.squareup.sqldelight")
}

kotlin {
    android()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.bundles.core)
                implementation(libs.bundles.sql)
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
    namespace = "com.github.nailkhaf.database"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}

sqldelight {
    database("Database") {
        packageName = "com.github.nailkhaf.database"
    }
}
