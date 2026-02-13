pipeline {
    agent any

    parameters {
        gitParameter branchFilter: 'origin/(.*)', defaultValue: 'master', name: 'BRANCH', type: 'PT_BRANCH'
    }
    tools {
        jdk 'jdk Android Studio'
    }

    environment {
        ANDROID_HOME = "/Users/karl/Library/Android/sdk"
        PATH = "${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator:${ANDROID_HOME}/cmdline-tools/latest/bin:${env.PATH}"
    }

    stages {
        stage('Check Java') {
            steps {
                sh 'java -version'
            }
        }

        stage('Ktlint Check') {
            steps {
                sh './gradlew ktlintCheck'
            }
        }

        stage('Build Debug') {
            steps {
                sh './gradlew clean assembleDebug'
            }
        }

        stage('Run Unit Test') {
            steps {
                sh './gradlew testDebugUnitTest'
            }
        }
    }
}
