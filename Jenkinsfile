pipeline {
    agent none

    stages {

        stage ('Docker Build') {
            agent { label 'DSK-841' }

            steps {
                echo 'Building image knovel-jmeter:1.0 '

                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }
        
        stage ('Running Tests') {
            agent { label 'DSK-841' }

            steps {

                sh './client.sh.'
            }
        }
    }
}