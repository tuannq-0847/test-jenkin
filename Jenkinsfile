pipeline {
    agent any

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
                sh '''
                  echo JAVA_HOME=$JAVA_HOME
                  which java
                  java -version
                '''
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
