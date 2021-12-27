pipeline {

    agent {
        label 'DSK-841'
    }

    environment {
        FOO = "bar"
        timestamp = sh(returnStdout: true, script: 'date +%Y%m%d_%H%M%S').trim()
        volume_path = sh(returnStdout: true, script: 'pwd').trim()
        jmeter_path = "/mnt/jmeter"
    }

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

                sh 'docker run \
                    --network host \
                    -v "${volume_path}":${jmeter_path} \
                    --rm \
                    knovel-jmeter:1.0 \
                    -n -X \
                    -Jclient.rmi.localport=7000 -Jserver.rmi.ssl.disable=true \
                    -t ${jmeter_path}/jmx/SearchSubstancesInternalSolr.jmx \
                    -l ${jmeter_path}/client/result_${timestamp}.csv \
                    -j ${jmeter_path}/client/jmeter_${timestamp}.log'
            }
        }
    }
}
