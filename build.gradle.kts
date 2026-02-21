// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.sonarqube.gradle.SonarExtension

plugins {
    alias(libs.plugins.gradle.versions)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.spotless) apply false
    id("org.sonarqube") version "7.2.2.6593"
    id("org.jetbrains.kotlinx.kover") version "0.9.7" apply false
}

sonar {
    properties {
        property("sonar.projectKey", "tuannq-0847_test-jenkin")
        property("sonar.projectName", "test-jenkin")
        property("sonar.organization", "tuannq-0847")
        property("sonar.sourceEncoding", "UTF-8")
        val appDebugReport = project(":app").layout.buildDirectory.file("reports/kover/reportDebug.xml").get().asFile.absolutePath
        val appTotalReport = project(":app").layout.buildDirectory.file("reports/kover/report.xml").get().asFile.absolutePath
        property("sonar.coverage.jacoco.xmlReportPaths", "$appDebugReport,$appTotalReport")
        property("sonar.branch", "master")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.token", "f1e397d18d2b933ebb472b16219c21a2c21e7b4f")
        property("sonar.inclusions", "**/*ViewModel.kt")
        property("sonar.test.inclusions", "**/*ViewModel*Test.kt")
    }
}

project(":app") {
    extensions.configure<SonarExtension> {
        properties {
            val debugReport = layout.buildDirectory.file("reports/kover/reportDebug.xml").get().asFile.absolutePath
            val totalReport = layout.buildDirectory.file("reports/kover/report.xml").get().asFile.absolutePath

            property(
                "sonar.coverage.jacoco.xmlReportPaths",
                "$debugReport,$totalReport"
            )
        }
    }
}

tasks.register("createSonarTmpDir") {
    doFirst {
        mkdir("${layout.buildDirectory.get()}/sonar/.sonartmp")
    }
}

tasks.named("sonar") {
    dependsOn("createSonarTmpDir")
    dependsOn(":app:koverXmlReportDebug")
}

