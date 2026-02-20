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
            defaultValue: true,
            description: 'Run SonarCloud analysis'
        )
        booleanParam(
            name: 'BUILD_DEBUG_APK',
            defaultValue: false,
            description: 'Build Debug APK'
        )
        booleanParam(
            name: 'WAIT_FOR_QUALITY_GATE',
            defaultValue: true,
            description: 'Wait for SonarCloud Quality Gate'
        )
    }

    tools {
        jdk 'jdk Android Studio'
    }

    environment {
        ANDROID_HOME = "/Users/karl/Library/Android/sdk"
        PATH = "${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator:${ANDROID_HOME}/cmdline-tools/latest/bin:${env.PATH}"

        SONARQUBE_SERVER = 'SonarQube'
        SONAR_PROJECT_KEY = 'tuannq-0847_test-jenkin'
        SONAR_HOST_URL = 'https://sonarcloud.io'
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
                    archiveArtifacts artifacts: 'app/build/outputs/apk/debug/*.apk',
                            fingerprint: true,
                            allowEmptyArchive: true
                }
            }
        }

        stage('Run Unit Test') {
            steps {
                sh './gradlew testDebugUnitTest --no-daemon'
            }
        }

        stage('SonarCloud Analysis') {
            when {
                expression { return params.RUN_SONARQUBE }
            }
            steps {
                withSonarQubeEnv("${SONARQUBE_SERVER}") {
                    sh './gradlew sonar --no-daemon'
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
                    script {
                        def qg = waitForQualityGate()

                        echo "=============================="
                        echo "Quality Gate Status: ${qg.status}"
                        echo "Sonar Dashboard:"
                        echo "${SONAR_HOST_URL}/dashboard?id=${SONAR_PROJECT_KEY}"
                        echo "Sonar Issues:"
                        echo "${SONAR_HOST_URL}/project/issues?id=${SONAR_PROJECT_KEY}"
                        echo "=============================="

                        if (qg.status != 'OK') {
                            error "Pipeline aborted due to Quality Gate failure: ${qg.status}"
                        }
                    }
                }
            }
        }
    }

    post {
        failure {
            echo "Build Failed ❌"
        }
        success {
            echo "Build Successful ✅"
        }
    }
}