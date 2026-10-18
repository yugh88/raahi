plugins {
    id("raahi.android.library")
    id("raahi.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "org.raahi.core.data"
}

// Bundled content packs, built from data/curated by packs/build.py.
androidComponents {
    onVariants { variant ->
        variant.sources.assets?.addStaticSourceDirectory(rootProject.file("../../packs/dist").absolutePath)
    }
}

dependencies {
    api(project(":core:model"))
    api(project(":core:engine"))
    implementation(project(":core:security"))
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.sqlite)
    implementation(libs.sqlcipher.android)
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.ext.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
