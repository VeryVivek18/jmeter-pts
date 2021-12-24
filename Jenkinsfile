pipeline {
    agent { label 'DSK-841' }

    stages {

        stage ('Docker Build') {
            steps {
                echo 'Building image knovel-jmeter:1.0 '

                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }
        
        stage ('Running Tests') {
            steps {

                sh './client.sh.'
            }
        }
    }
}