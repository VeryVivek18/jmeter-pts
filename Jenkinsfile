pipeline {
    agent any
    stages {
        stage('Docker Build') {
            steps {
                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }
    }
}