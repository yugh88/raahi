plugins {
    id("raahi.android.library")
}

android {
    namespace = "org.raahi.core.media"
}

dependencies {
    implementation(project(":core:model"))
}
