plugins {
    id("raahi.android.library")
}

android {
    namespace = "org.raahi.core.voice"
}

dependencies {
    implementation(project(":core:model"))
}
