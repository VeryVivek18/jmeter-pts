pipeline {
    agent any
     parameters {
//             choice(
//                 choices: ['All', 'SearchSubstancesInternalSolr' , 'SearchSubstancesApi'],
//                 description: 'Select file for perticular test otherwise all test will be performed.',
//                 name: 'REQUESTED_FILE')
//             multiselect(
//                 configuration: ['H,Type,Api',
//                                 'V,SELECTED_TYPE,SELECTED_API',
//                                 'C,JMeter,SearchSubstancesInternalSolr',
//                                 'C,JMeter,SearchSubstancesApi',
//                                 'C,Direct,Rumelner TV',
//                                 'C,Direct,FC Rumeln'
//                                 ]
//                 description: 'Select file for perticular test otherwise all test will be performed.',
//                 name: 'REQUESTED_FILE1')
//             )

              extendedChoice description: 'select api to build image',
                             multiSelectDelimiter: ',',
                             name: 'API Selection',
                             propertyFile: env.PROPERTIES_FILE,
                             quoteValue: false, saveJSONParameterToFile: false,
                             type: 'PT_MULTI_LEVEL_SINGLE_SELECT',
                             value: 'Type,Api',
                             visibleItemCount: 5


        }

    environment {
            APPLICATION = "SearchSubstancesInternalSolr,SearchSubstancesApi"
            PROPERTIES_FILE = "${WORKSPACE}/ApiList.properties"
            timestamp = sh(returnStdout: true, script: 'date +%Y%m%d_%H%M%S').trim()
            volume_path = sh(returnStdout: true, script: 'pwd').trim()
            jmeter_path = "/mnt/jmeter"
            BUILD_ID = "${env.JOB_NAME}-${env.BUILD_NUMBER}"
            RESULT_PATH = "${BUILD_ID}-${timestamp}"
            UID = sh(returnStdout: true ,script: "(id -u)").trim()
            GID = sh(returnStdout: true ,script: "(id -g)").trim()
            UGID = "${UID}:${GID}"
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
                            -l ${jmeter_path}/client/'+env.RESULT_PATH+'/'+server+'-result_${timestamp}.csv \
                            -j ${jmeter_path}/client/'+env.RESULT_PATH+'/'+server+'-jmeter_${timestamp}.log'
                    }
                }
            }
        }
//         stage ('Test Report Handling') {
//             steps {
//                 sh 'zip -r client/' + env.RESULT_PATH + '.zip client/'+env.RESULT_PATH
//                 emailext (
//                     subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}'",
//                     body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
//                     attachmentsPattern: 'client/' + env.RESULT_PATH  +'.zip',
//                     mimeType: 'text/html',
//                     to: "vivek.topiya@thegatewaycorp.co.in",
//                     from: "jmeter-pts@thegatewaycorp.co.in"
//                 )
//             }
//         }
    }
    post {
        success {
           sh 'zip -r client/' + env.RESULT_PATH + '.zip client/'+env.RESULT_PATH
           emailext (
               subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}' Successful",
               body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
               attachmentsPattern: 'client/' + env.RESULT_PATH  +'.zip',
               mimeType: 'text/html',
               to: "vivek.topiya@thegatewaycorp.co.in",
               from: "jmeter-pts@thegatewaycorp.co.in"
           )
        }
        failure {
           emailext (
              subject: "Job '${env.JOB_NAME} ${env.BUILD_NUMBER}' Failed",
              body: """<p>Check console output at <a href="${env.BUILD_URL}">${env.JOB_NAME}</a></p>""",
              mimeType: 'text/html',
              to: "vivek.topiya@thegatewaycorp.co.in",
              from: "jmeter-pts@thegatewaycorp.co.in"
          )
        }
    }
}