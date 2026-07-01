pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Finance tracker"
include(":app")
include(":core:model")
include(":core:designsystem")
include(":core:ui")
include(":core:data")
include(":core:datastore")
include(":core:database")
include(":core:network")
include(":feature:auth")
include(":feature:registration")
