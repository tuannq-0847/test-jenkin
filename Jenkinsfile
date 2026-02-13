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
    }

    tools {
        jdk 'jdk Android Studio'
    }

    environment {
        ANDROID_HOME = "/Users/karl/Library/Android/sdk"
        PATH = "${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator:${ANDROID_HOME}/cmdline-tools/latest/bin:${env.PATH}"
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
            steps {
                sh './gradlew clean assembleDebug --no-daemon'
            }
        }

        stage('Run Unit Test') {
            steps {
                sh './gradlew testDebugUnitTest --no-daemon'
            }
        }
    }
}
