pipeline {
    agent none

    stages {

        stage ('Docker Build') {
            agent any

            steps {
                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }
        
        stage ('Running Jmeter Tests') {
            agent any

            steps {

                sh 'chmod +777 client.sh'
                sh 'sh ./client.sh'
            }
        }
    }
}