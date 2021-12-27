pipeline {
    agent any

    stages {

        stage ('Docker Build') {

            steps {
                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }

        stage ('Running Jmeter Tests') {


            steps {
//                 sh 'docker run --rm  knovel-jmeter:1.0 '
                sh 'chmod +x ./client.sh'
                sh './client.sh'
            }
        }
    }
}