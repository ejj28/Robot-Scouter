import org.jetbrains.kotlin.gradle.internal.CacheImplementation
import java.io.FileInputStream
import java.util.Properties

plugins {
    id("io.fabric")
    id("com.github.triplet.play")
}
if (isReleaseBuild) apply(plugin = "com.google.firebase.firebase-perf")
crashlytics.alwaysUpdateBuildId = isReleaseBuild

android {
    dynamicFeatures = rootProject.project("feature").childProjects.mapTo(mutableSetOf()) {
        ":feature:${it.key}"
    }

    defaultConfig {
        applicationId = "com.supercilex.robotscouter"
        versionName = "2.3.1"
        multiDexEnabled = true
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = file("keystore.properties")
            val keystoreProperties = Properties()
            keystoreProperties.load(FileInputStream(keystorePropertiesFile))

            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }

        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            setProguardFiles(listOf(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    file("proguard-rules.pro")
            ))

            // TODO Crashlytics doesn't support the new DSL yet so we need to downgrade
//            postprocessing {
//                removeUnusedCode true
//                removeUnusedResources true
//                obfuscate true
//                optimizeCode true
//                proguardFile 'proguard-rules.pro'
//            }
        }
    }
}

play {
    serviceAccountCredentials = file("google-play-auto-publisher.json")
    track = "alpha"
    resolutionStrategy = "auto"
    outputProcessor = { versionNameOverride = "$versionNameOverride.$versionCode" }
    defaultToAppBundles = true
}

dependencies {
    implementation(project(":library:shared"))
    implementation(project(":library:shared-scouting"))

    implementation(Config.Libs.Jetpack.multidex)
    implementation(Config.Libs.PlayServices.playCore)
    implementation(Config.Libs.Misc.billing)

    implementation(Config.Libs.Firebase.perf)
    implementation(Config.Libs.Firebase.invites)

    // TODO remove when Firebase updates their deps
    implementation(Config.Libs.Misc.gson) // Override Firestore
    implementation("com.squareup.okio:okio:1.15.0")
}

apply(plugin = "com.google.gms.google-services")
