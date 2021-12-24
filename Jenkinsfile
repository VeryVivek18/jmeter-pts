pipeline {
    agent none

    stages {

        stage ('Docker Build') {
            agent any

            steps {
                echo 'Building image knovel-jmeter:1.0 '

                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }
        
        stage ('Running Tests') {
            agent any

            steps {

                sh './client.sh.'
            }
        }
    }
}