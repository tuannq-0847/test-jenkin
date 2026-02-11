pipeline {
    agent any

    environment {
        ANDROID_HOME = "/Users/karl/Library/Android/sdk"
        PATH = "${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/emulator:${ANDROID_HOME}/cmdline-tools/latest/bin:${env.PATH}"
    }

    stages {
        stage('Check Java') {
            steps {
                sh 'java -version'
                sh 'echo $JAVA_HOME'
            }
        }

        stage('Build Debug') {
            steps {
                sh 'echo ANDROID_HOME=$ANDROID_HOME'
                sh 'ls $ANDROID_HOME/platforms'
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
