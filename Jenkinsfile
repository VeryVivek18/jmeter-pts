pipeline {
    agent any
     parameters {
            choice(
                choices: ['All', 'SearchSubstancesInternalSolr' , 'SearchSubstancesApi'],
                description: 'Select file for perticular test otherwise all test will be performed.',
                name: 'REQUESTED_FILE')
        }

    environment {
            APPLICATION = "SearchSubstancesInternalSolr,SearchSubstancesApi"
            timestamp = sh(returnStdout: true, script: 'date +%Y%m%d_%H%M%S').trim()
            volume_path = sh(returnStdout: true, script: 'pwd').trim()
            jmeter_path = "/mnt/jmeter"
            BUILD_ID = "${env.JOB_NAME}-${env.BUILD_NUMBER}"
            RESULT_PATH = "${BUILD_ID}-${timestamp}"
            UID = sh(script: "(id -u)", returnStdout: true)
            GID = sh(script: "(id -g)", returnStdout: true)
            UID2 = UID.replaceAll("\r\n|\n\r|\n|\r", "")
            GID2 = GID.replaceAll("\r\n|\n\r|\n|\r", "")
            UGID = UID2 + ":" + GID2
        }

    stages {

        stage ('Docker Build') {

            steps {
                sh 'docker build -t knovel-jmeter:1.0 .'
            }
        }

        stage ('Parameterized Build') {
         when {
            // Only say hello if a "greeting" is requested
            expression { params.REQUESTED_FILE != 'All' }
        }
            steps {
                script{

                    println("group id is : ${UID2}:${GID2}")
                    println("group id is : ${UGID}")
                    sh 'docker run \
                        --user ' + env.UGID +' \
                        --network host \
                        -v "${volume_path}":${jmeter_path} \
                        --rm \
                        knovel-jmeter:1.0 \
                        -n -X \
                        -Jclient.rmi.localport=7000 -Jserver.rmi.ssl.disable=true \
                        -t ${jmeter_path}/jmx/'+params.REQUESTED_FILE+'.jmx \
                        -l ${jmeter_path}/client/'+env.RESULT_PATH+'/'+params.REQUESTED_FILE+'-result_${timestamp}.csv \
                        -j ${jmeter_path}/client/'+env.RESULT_PATH+'/'+params.REQUESTED_FILE+'jmeter_${timestamp}.log'
                }
            }
        }

        stage ('Full Build') {
            when {
                // Only say hello if a "greeting" is requested
                expression { params.REQUESTED_FILE == 'All' }
            }
            steps {
                script {
                    env.APPLICATION.tokenize(",").each { server ->
                        sh 'docker run \
                            --user ' + UGID +' \
                            --network host \
                            -v "${volume_path}":${jmeter_path} \
                            --rm \
                            knovel-jmeter:1.0 \
                            -n -X \
                            -Jclient.rmi.localport=7000 -Jserver.rmi.ssl.disable=true \
                            -t ${jmeter_path}/jmx/SearchSubstancesInternalSolr.jmx \
                            -l ${jmeter_path}/client/'+$server+'/internal-solr-result_${timestamp}.csv \
                            -j ${jmeter_path}/client/'+$server+'/internal-solr-jmeter_${timestamp}.log'
                    }
                }
            }
        }

        stage ('Test Report Handling') {
            steps {
                sh 'zip -r client/' + env.RESULT_PATH + '.zip client/'+env.RESULT_PATH
                emailext (
                    subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}'",
                    body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
                    attachmentsPattern: 'client/' + env.RESULT_PATH  +'.zip',
                    mimeType: 'text/html',
                    to: "vivek.topiya@thegatewaycorp.co.in",
                    from: "jmeter-pts@thegatewaycorp.co.in"
                )
            }
        }
    }
}