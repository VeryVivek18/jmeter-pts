pipeline {
    agent any

    environment {
            timestamp = sh(returnStdout: true, script: 'date +%Y%m%d_%H%M%S').trim()
            volume_path = sh(returnStdout: true, script: 'pwd').trim()
            jmeter_path = "/mnt/jmeter"
            BUILD_ID = "${env.JOB_NAME}-${env.BUILD_NUMBER}"
            RESULT_PATH = "${BUILD_ID}-${timestamp}"
        }

    stages {

        stage ('Docker Build') {

            steps {
                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }

        stage ('Running Jmeter Tests') {
            steps {
                script{
                    UID = sh(script: "(id -u)", returnStdout: true)
                    GID = sh(script: "(id -g)", returnStdout: true)
                    UID2 = UID.replaceAll("\r\n|\n\r|\n|\r", "")
                    GID2 = GID.replaceAll("\r\n|\n\r|\n|\r", "")
                    UGID = UID2 + ":" + GID2
                    println("group id is : ${UID2}:${GID2}")
                    println("group id is : ${UGID}")
                    sh 'docker run \
                        --user ' + UGID +' \
                        --network host \
                        -v "${volume_path}":${jmeter_path} \
                        --rm \
                        knovel-jmeter:1.0 \
                        -n -X \
                        -Jclient.rmi.localport=7000 -Jserver.rmi.ssl.disable=true \
                        -t ${jmeter_path}/jmx/SearchSubstancesInternalSolr.jmx \
                        -l ${jmeter_path}/client/'+env.RESULT_PATH+'/result_${timestamp}.csv \
                        -j ${jmeter_path}/client/'+env.RESULT_PATH+'/jmeter_${timestamp}.log'
                }
            }
        }

        stage ('Test Report Handling') {
            steps {
                sh 'ls client'
                sh 'zip -r client/' + env.RESULT_PATH + '.zip client/'+env.RESULT_PATH
                emailext (
                    subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}'",
                    body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
                    attachmentsPattern: 'client/' + env.RESULT_PATH  +'.zip',
                    mimeType: 'text/html',
                    to: "vivek.topiya@thegatewaycorp.co.in",
                    from: "vivek.topiya@thegatewaycorp.co.in"
                )
            }
        }
    }
//     post {
//         failure {
//             emailext attachmentsPattern: 'test.zip', body: '''${SCRIPT, template="groovy-html.template"}''',
//                     subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - Failed",
//                     mimeType: 'text/html',to: "email id"
//         }
//         success {
//            emailext attachmentsPattern: 'test.zip', body: '''${SCRIPT, template="groovy-html.template"}''',
//                 subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - Successful",
//                 mimeType: 'text/html',to: "email id"
//         }
//     }
}