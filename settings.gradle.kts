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
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.5"
////                            # available:"0.60.6"
}

rootProject.name = "Themed Manager"
include(":manager")
include(":autorefreshrate")
include(":PerAppDownscale")
include(":PXLSRTR")
include(":filesizes")

include(":audhdlauncher")
include(":mtk-bpf-patcher")
