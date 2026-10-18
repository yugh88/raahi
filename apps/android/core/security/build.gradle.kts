plugins {
    id("raahi.android.library")
}

android {
    namespace = "org.raahi.core.security"
}

dependencies {
    implementation(project(":core:model"))
}
