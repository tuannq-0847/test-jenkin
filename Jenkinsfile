pipeline {
    agent any

    parameters {
        gitParameter(
            name: 'BRANCH',
            type: 'PT_BRANCH',
            defaultValue: 'master',
            branchFilter: 'origin/(.*)',
            sortMode: 'DESCENDING_SMART'
        )
        booleanParam(
            name: 'RUN_SONARQUBE',
            defaultValue: false,
            description: 'Run SonarQube analysis'
        )
        booleanParam(
            name: 'BUILD_DEBUG_APK',
            defaultValue: true,
            description: 'Build Debug APK (assembleDebug)'
        )
        booleanParam(
            name: 'WAIT_FOR_QUALITY_GATE',
            defaultValue: false,
            description: 'Wait for SonarQube Quality Gate (requires webhook configuration)'
        )
    }

    tools {
        jdk 'jdk Android Studio'
    }

    environment {
        ANDROID_HOME = "/Users/karl/Library/Android/sdk"
        PATH = "${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator:${ANDROID_HOME}/cmdline-tools/latest/bin:${env.PATH}"

        // Jenkins global config: Manage Jenkins -> Configure System -> SonarQube servers
        // Use the exact SonarQube server name below.
        SONARQUBE_SERVER = 'SonarQube'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${params.BRANCH}"]],
                    userRemoteConfigs: [[
                        url: 'https://github.com/tuannq-0847/test-jenkin.git'
                    ]]
                ])
            }
        }

        stage('Check Java') {
            steps {
                sh 'java -version'
            }
        }

        stage('Ktlint Check') {
            steps {
                sh './gradlew ktlintCheck --no-daemon'
            }
        }

        stage('Build Debug') {
            when {
                expression { return params.BUILD_DEBUG_APK }
            }
            steps {
                sh './gradlew clean assembleDebug --no-daemon'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk', fingerprint: true, allowEmptyArchive: true
                }
            }
        }

        stage('Run Unit Test') {
            steps {
                sh './gradlew testDebugUnitTest --no-daemon'
            }
        }

        stage('SonarQube Analysis') {
            when {
                expression { return params.RUN_SONARQUBE }
            }
            steps {
                withSonarQubeEnv(env.SONARQUBE_SERVER) {
                    sh './gradlew sonarqube --no-daemon'
                }
            }
        }

        stage('Quality Gate') {
            when {
                allOf {
                    expression { return params.RUN_SONARQUBE }
                    expression { return params.WAIT_FOR_QUALITY_GATE }
                }
            }
            steps {
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
    }
}
