import java.text.SimpleDateFormat

def call(Map stageParams) {
    //Setting Date
    def dateFormat = new SimpleDateFormat("dd-MM-yyyy")
    def date = dateFormat.format(new Date())

    //Providing execute permissions to mvnw
    sh "chmod +x ./mvnw"

    //Reading version
    def rel_version = sh(returnStdout: true, script: "./mvnw org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout").trim()

    for (element in mapToList(stageParams["commands"])) {
        sh "./mvnw ${element[1]} -Drelease.version=${rel_version}"
    }
}

// Required due to JENKINS-27421
@NonCPS
def List<List<?>> mapToList(Map map) {
  return map.collect { it ->
    [it.key, it.value]
  }
}

