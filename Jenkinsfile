pipeline {
    agent any
    parameters {
        choice(
                choices: ['SearchSubstancesInternalSolr', 'SearchSubstancesApi'],
                description: 'Select file for particular test',
                name: 'REQUESTED_FILE')
    }
    environment {
        timestamp = sh(returnStdout: true, script: 'date +%Y%m%d_%H%M%S').trim()
        volume_path = sh(returnStdout: true, script: 'pwd').trim()
        jmeter_path = "/mnt/jmeter"
        BUILD_ID = "${env.JOB_NAME}-${env.BUILD_NUMBER}"
        RESULT_PATH = "${BUILD_ID}-${timestamp}"
        UID = sh(returnStdout: true, script: "(id -u)").trim()
        GID = sh(returnStdout: true, script: "(id -g)").trim()
        UGID = "${UID}:${GID}"
        MAIL_TO = "vivek.topiya@thegatewaycorp.co.in"
        MAIL_FROM = "jmeter-pts@thegatewaycorp.co.in"
    }
    stages {
        stage('Docker Build') {
            steps {
                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }
        stage('Parameterized Build') {
            steps {
                script {
                    sh 'docker run \
                        --user ' + env.UGID + ' \
                        --network host \
                        -p 8080:8080 \
                        -v "${volume_path}":${jmeter_path} \
                        --rm \
                        knovel-jmeter:1.0 \
                        -n -X \
                        -Jclient.rmi.localport=7000 -Jserver.rmi.ssl.disable=true \
                        -t ${jmeter_path}/jmx/' + params.REQUESTED_FILE + '.jmx \
                        -l ${jmeter_path}/client/' + env.RESULT_PATH + '/' + params.REQUESTED_FILE + '-result_${timestamp}.csv \
                        -j ${jmeter_path}/client/' + env.RESULT_PATH + '/' + params.REQUESTED_FILE + 'jmeter_${timestamp}.log'
                }
            }
        }
    }
    post {
        success {
            sh 'zip -r client/' + env.RESULT_PATH + '.zip client/' + env.RESULT_PATH
            emailext(
                    subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}' Successful",
                    body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
                    attachmentsPattern: 'client/' + env.RESULT_PATH + '.zip',
                    mimeType: 'text/html',
                    to: env.MAIL_TO,
                    from: env.MAIL_FROM
            )
        }
        failure {
            emailext(
                    subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}' Failed",
                    body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
                    mimeType: 'text/html',
                    to: env.MAIL_TO,
                    from: env.MAIL_FROM
            )
        }
    }
}