pipeline {
    agent any

    environment {
        ANDROID_HOME = "${HOME}/Library/Android/sdk"
        PATH = "${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/tools:${ANDROID_HOME}/tools/bin:${env.PATH}"
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo.git'
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

        stage('Archive APK') {
            steps {
                archiveArtifacts artifacts: 'app/build/outputs/**/*.apk', fingerprint: true
            }
        }
    }
}
