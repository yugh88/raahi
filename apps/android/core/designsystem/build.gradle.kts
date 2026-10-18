plugins {
    id("raahi.android.library")
    id("raahi.android.compose")
    id("raahi.roborazzi")
}

android {
    namespace = "org.raahi.core.designsystem"
}

dependencies {
    api(project(":core:model"))
    implementation(libs.androidx.core.ktx)
    testImplementation(project(":core:testing"))
}
