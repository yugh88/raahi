pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "raahi"

include(":app")
include(
    ":core:model",
    ":core:designsystem",
    ":core:data",
    ":core:sync",
    ":core:security",
    ":core:voice",
    ":core:engine",
    ":core:media",
    ":core:testing",
)
include(
    ":feature:onboarding",
    ":feature:home",
    ":feature:roadmap",
    ":feature:learn",
    ":feature:courses",
    ":feature:askdidi",
    ":feature:earn",
    ":feature:progress",
    ":feature:events",
    ":feature:teacher",
)
