// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.spotless) apply false
    id("org.sonarqube") version "7.2.2.6593"
    id("org.jetbrains.kotlinx.kover") version "0.7.5" apply false
}

sonar {
    properties {
        property("sonar.projectKey", "tuannq-0847_test-jenkin")
        property("sonar.projectName", "test-jenkin")
        property("sonar.organization", "tuannq-0847")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.branch", "master")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.token", "f1e397d18d2b933ebb472b16219c21a2c21e7b4f")
        property(
            "sonar.inclusions",
            "app/src/main/java/**/*ViewModel.kt"
        )
        property("sonar.test.inclusions", "**/*ViewModel*Test.kt")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${buildDir}/reports/kover/xml/report.xml"
        )

    }
}
