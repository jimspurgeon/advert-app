// Retarget — root build file (Kotlin DSL)
// AGP and tooling versions are pinned centrally in gradle/libs.versions.toml.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
}
