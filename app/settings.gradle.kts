rootProject.name = "OmnisMytho"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/apegroup/revolver")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GH_USERNAME") ?: ""
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GH_TOKEN") ?: ""
            }
        }
    }
}

include(":composeApp")