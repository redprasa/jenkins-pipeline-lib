def call(Map stageParams) {
    //Setting variables
    def projectKey = stageParams["projectKey"]
    def projectName = stageParams["projectName"]

    //Reading version
    // rel_version = sh(returnStdout: true, script: "head -n 1 version")
    rel_version = sh(returnStdout: true, script: "./mvnw org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout").trim()
    mvnw_path = '.'

    //Sonar URL
    def sonar_url = "http://localhost:9000"

    //Providing execute permissions to mvnw
    sh "chmod 775 ${WORKSPACE}"
    sh "chmod +x ${mvnw_path}/mvnw"

    //Getting credentails from Jenkins
    withCredentials([string(credentialsId: 'sonar_auth_token', variable: 'token')]) {

        //Running sonar maven goal
        sh "${mvnw_path}/mvnw sonar:sonar -f pom.xml -Dsonar.projectKey=${projectKey} -Dsonar.projectName='${projectName}' -Dsonar.login=${token} -Dsonar.host.url=${sonar_url} -Drelease.version=${rel_version}"

        //Quality Gate Check Start
        //Reading sonar report
        def props = readProperties  file:"target/sonar/report-task.txt"
        echo "properties = ${props}"
        def sonarServerUrl = props["serverUrl"]
        def ceTaskUrl = props["ceTaskUrl"]
        def ceTask

        //Check for Analysis status.
        //If analysis is not complete within 2 minute then timeout
        timeout(time: 2, unit: 'MINUTES') {
            waitUntil {
                sh "curl -u ${token} ${ceTaskUrl} -o ceTask.json"
                ceTask = readJSON file:'ceTask.json'
                echo ceTask.toString()
                return "SUCCESS".equals(ceTask["task"]["status"])
            }
        }

        //Once the analysis is done, check for Quality gate status
        def qualityGateUrl = sonar_url + "/api/qualitygates/project_status?analysisId=" + ceTask["task"]["analysisId"]
        sh "curl -u ${token} ${qualityGateUrl} -o qualityGate.json"
        def qualitygate = readJSON file:"qualityGate.json"
        echo qualitygate.toString()
        //Fail the build is Quality Gate is not passed

    //    if ("ERROR".equals(qualitygate["projectStatus"]["status"])) {
    //        error  "Quality Gate failure"
    //    }
    }
}

