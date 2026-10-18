plugins {
    id("raahi.jvm.library")
}

dependencies {
    api(project(":core:model"))
}

// Engine tests run against the real curated files in data/curated, never against made-up fixtures.
tasks.withType<Test>().configureEach {
    systemProperty("raahi.curated", rootProject.file("../../data/curated").absolutePath)
}
