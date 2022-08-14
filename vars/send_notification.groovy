def call(Map stageParams) {
  // build status of null means successful
  buildStatus = stageParams["env"].currentBuild.result ?: 'SUCCESS'

  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def jobname = stageParams["env"].env.JOB_NAME
  def appname = (jobname.split("/").length == 3 ? jobname.split("/")[2] : jobname.split("/")[1])
  def branchname = stageParams["env"].params.branch
  def subject = "BUILD ${buildStatus}-> APP: ${appname} BRANCH: ${branchname} #${stageParams["env"].env.BUILD_NUMBER}"
  def summary = "${subject} (<${stageParams["env"].env.BUILD_URL}|Open>)"
  def details = """<p>STARTED: Job '${jobname} [${stageParams["env"].env.BUILD_NUMBER}]':</p>
    <p>Check console output at &QUOT;<a href='${stageParams["env"].env.BUILD_URL}'>${jobname} [${stageParams["env"].env.BUILD_NUMBER}]</a>&QUOT;</p>"""
  def content = '${JELLY_SCRIPT, template="detailed_email.jelly"}'

  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESS') {
    color = 'GREEN'
    colorCode = '#00FF00'

  } else {
    color = 'RED'
    colorCode = '#FF0000'

  }

 emailext (
      subject: subject,
      body: content,
      mimeType: 'text/html',
      to: 'sbrpdevops@gmail.com',
      recipientProviders: [[$class: 'RequesterRecipientProvider']]
    )

}

