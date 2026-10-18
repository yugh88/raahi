plugins {
    id("raahi.android.library")
}

android {
    namespace = "org.raahi.core.data"
}

dependencies {
    implementation(project(":core:model"))
}
