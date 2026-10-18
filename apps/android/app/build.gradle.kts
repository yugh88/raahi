plugins {
    id("raahi.android.application")
    id("raahi.android.compose")
    id("raahi.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "org.raahi.app"

    defaultConfig {
        applicationId = "org.raahi"
        versionCode = 1
        versionName = "0.1.0"
    }

    // One APK per ABI; each must stay under 25 MB (checked in CI by tools/apk-size-check.sh).
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Demo builds are signed with the debug key until a release key is set up (Phase 9).
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        buildConfig = true
    }

    androidResources {
        localeFilters += listOf("en", "hi", "ta", "te", "kn", "ml")
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))
    implementation(project(":core:voice"))
    listOf("onboarding", "home", "roadmap", "learn", "courses", "askdidi", "earn", "progress", "events", "teacher")
        .forEach { implementation(project(":feature:$it")) }
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
