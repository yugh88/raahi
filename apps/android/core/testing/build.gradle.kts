plugins {
    id("raahi.android.library")
    id("raahi.android.compose")
}

android {
    namespace = "org.raahi.core.testing"
}

dependencies {
    api(project(":core:designsystem"))
    api(platform(libs.compose.bom))
    api(libs.junit)
    api(libs.robolectric)
    api(libs.roborazzi)
    api(libs.roborazzi.compose)
    api(libs.roborazzi.junit.rule)
    api(libs.compose.ui.test.junit4)
    api(libs.androidx.test.ext.junit)
    api(libs.androidx.test.espresso.core)
}
