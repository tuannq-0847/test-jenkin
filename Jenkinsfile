pipeline {
    agent any

    stages {

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
