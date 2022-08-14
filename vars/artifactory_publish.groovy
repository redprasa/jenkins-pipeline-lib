import java.text.SimpleDateFormat

def call(Map stageParams) {
    //Setting variables
    def dateFormat = new SimpleDateFormat("dd-MM-yyyy")
    def date = dateFormat.format(new Date())
    def artifactory = "http://localhost:8081/artifactory/"
    def appName = stageParams["appName"]
    def targetPrefix = stageParams["targetPrefix"]
    def snapshotRepo = stageParams["snapshotRepo"]
    def target = (stageParams["branchName"].equals("master")|| stageParams["branchName"].startsWith("release/") ) ? "${targetPrefix}-release" : "${targetPrefix}-${snapshotRepo}"
    def rel_version = sh(returnStdout: true, script: './mvnw org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout').trim()
     def release_version = "${rel_version}"

    //Getting credentials from Jenkins
    withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'artifactory_auth',
                        usernameVariable: 'username', passwordVariable: 'password']]) {
        def server = Artifactory.newServer url: "${artifactory}", username: "${username}", password: "${password}"
        def uploadSpec = """{
                                "files": [{
                                    "pattern": "target/${appName}-${release_version}.jar",
                                    "target": "${target}"
                                }]
                            }"""
        def buildInfo = server.upload spec: uploadSpec
        server.publishBuildInfo buildInfo
    }
}
