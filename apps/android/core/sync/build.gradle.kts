plugins {
    id("raahi.android.library")
}

android {
    namespace = "org.raahi.core.sync"
}

dependencies {
    implementation(project(":core:model"))
}
