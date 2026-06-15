// ============================================
// ⚙️ Edham Android - Settings
// ============================================

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "edham-logistics"

include(":app")

// Enable Gradle build cache (temporarily disabled to fix file locking issues)
buildCache {
    local {
        isEnabled = false
        directory = File(rootDir, "build-cache")
    }
}


println("🚀 Edham Logistics Android Settings Loaded")
