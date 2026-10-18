plugins {
    `kotlin-dsl`
}

group = "org.raahi.buildlogic"

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.compose.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
    compileOnly(libs.hilt.gradle.plugin)
    compileOnly(libs.roborazzi.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "raahi.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "raahi.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "raahi.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidFeature") {
            id = "raahi.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("hilt") {
            id = "raahi.hilt"
            implementationClass = "HiltConventionPlugin"
        }
        register("roborazzi") {
            id = "raahi.roborazzi"
            implementationClass = "RoborazziConventionPlugin"
        }
        register("jvmLibrary") {
            id = "raahi.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
