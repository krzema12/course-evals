plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        withJava()
    }
    js(IR) {
        browser()
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.coroutines)
                implementation(libs.serialization)
                implementation(libs.bundles.ktor.main)
                implementation(project(":common"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }
    }
}

application {
    mainClass.set("io.github.opletter.courseevals.rutgers.MainKt")
}