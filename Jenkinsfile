pipeline {
    agent any

    environment {
            timestamp = sh(returnStdout: true, script: 'date +%Y%m%d_%H%M%S').trim()
            volume_path = sh(returnStdout: true, script: 'pwd').trim()
            jmeter_path = "/mnt/jmeter"
        }

    stages {

        stage ('Docker Build') {

            steps {
                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }

        stage ('Running Jmeter Tests') {
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

        stage ('Test Aggregation') {
            steps {
                sh 'ls ${volume_path}/client/'
                sh 'zip -r "${env.JOB_NAME}-${env.BUILD_NUMBER}.zip ${volume_path}/client/"'
                sh 'ls -lrt'
//                 emailext (
//                     subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}'",
//                     body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
//                     attachmentsPattern: '*.zip',
//                     to: "vivek.topiya@thegatewaycorp.co.in",
//                     from: "vivek.topiya@thegatewaycorp.co.in"
//                 )
            }
        }
    }
}