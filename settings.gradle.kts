@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val composeVersion: String = version("compose", "1.4.0")
            val ktorVersion: String = version("ktor", "2.2.4")
            val serializationVersion: String = version("serialization", "1.5.0")
            val koinVersion: String = version("koin", "3.4.0")
            val sqlVersion: String = version("sql", "1.5.5")

            library("androidx.ktx", "androidx.core:core-ktx:1.9.0")
            library("androidx.lifecycle", "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
            library("androidx.activity.compose", "androidx.activity:activity-compose:1.7.0")

            library("compose.ui", "androidx.compose.ui", "ui").versionRef(composeVersion)
            library(
                "compose.ui.material",
                "androidx.compose.material3",
                "material3"
            ).version("1.1.0-beta02")
            library(
                "compose.ui.tooling.preview",
                "androidx.compose.ui",
                "ui-tooling-preview"
            ).versionRef(composeVersion)
            library("compose.ui.tooling", "androidx.compose.ui", "ui-tooling").versionRef(
                composeVersion
            )
            library(
                "compose.ui.test.manifest",
                "androidx.compose.ui",
                "ui-test-manifest"
            ).versionRef(composeVersion)
            library("compose.ui.test.juint4", "androidx.compose.ui", "ui-test-junit4").versionRef(
                composeVersion
            )

            library("ktor", "io.ktor", "ktor-client-core").versionRef(ktorVersion)
            library("ktor.content", "io.ktor", "ktor-client-content-negotiation").versionRef(
                ktorVersion
            )
            library("ktor.json", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(
                ktorVersion
            )
            library("ktor.compress", "io.ktor", "ktor-client-encoding").versionRef(ktorVersion)
            library("ktor.okhttp", "io.ktor", "ktor-client-okhttp").versionRef(ktorVersion)
            library("ktor.mock", "io.ktor", "ktor-client-mock").versionRef(ktorVersion)
            library("okio", "com.squareup.okio:okio:3.3.0")

            library("koin", "io.insert-koin", "koin-core").versionRef(koinVersion)
            library("koin.android", "io.insert-koin", "koin-android").versionRef(koinVersion)
            library("koin.test", "io.insert-koin", "koin-test").versionRef(koinVersion)

            library(
                "kotlinx.json",
                "org.jetbrains.kotlinx",
                "kotlinx-serialization-json"
            ).versionRef(serializationVersion)
            library("coroutines", "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")

            library("junit", "junit:junit:4.13.2")
            library("androidx.junit", "androidx.test.ext:junit:1.1.5")
            library("espresso.core", "androidx.test.espresso:espresso-core:3.5.1")
            library("mockk", "io.mockk:mockk:1.13.4")

            library("bignum", "com.ionspin.kotlin:bignum:0.3.8")
            library("bcprov", "org.bouncycastle:bcprov-jdk18on:1.72")
            library("atomicfu", "org.jetbrains.kotlinx:atomicfu:0.18.5")

            library("sql.core", "com.squareup.sqldelight", "runtime").versionRef(sqlVersion)
            library(
                "sql.coroutines",
                "com.squareup.sqldelight",
                "coroutines-extensions"
            ).versionRef(sqlVersion)
            library("sql.android", "com.squareup.sqldelight", "android-driver")
                .versionRef(sqlVersion)
            library("sql.jvm", "com.squareup.sqldelight", "sqlite-driver").versionRef(sqlVersion)
            library("wire", "com.squareup.wire:wire-runtime:4.4.3")
            library("datastore", "androidx.datastore:datastore-core-okio:1.1.0-alpha03")
            library("decompose", "com.arkivanov.decompose:decompose:1.0.0")

            bundle(
                "androidx",
                listOf("androidx.ktx", "androidx.lifecycle", "androidx.activity.compose")
            )
            bundle(
                "compose",
                listOf("compose.ui", "compose.ui.tooling.preview", "compose.ui.material")
            )
            bundle("sql", listOf("sql.core", "sql.coroutines"))
            bundle("core", listOf("coroutines", "bignum", "okio", "atomicfu", "koin"))
            bundle("ktor", listOf("ktor", "ktor.content", "ktor.json", "ktor.compress"))
            bundle("debug", listOf("compose.ui.tooling", "compose.ui.test.manifest"))
            bundle("test", listOf("koin.test"))
            bundle(
                "android.test",
                listOf("androidx.junit", "espresso.core", "compose.ui.test.juint4")
            )
        }
    }
}

rootProject.name = "Tokens Feed"
include(":app")

include(":feature:balances")
include(":feature:transfers")
include(":feature:account")

include(":data:account")
include(":data:tokens")

include(":core:web3")
include(":core:database")
include(":core:datastore")
