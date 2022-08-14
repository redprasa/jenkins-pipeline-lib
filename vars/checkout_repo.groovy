def call(Map stageParams){
    //Setting up variables
    def branchName = stageParams["branchName"]
    def appName = stageParams["appName"]

    dir("${appName}") {
        checkout(
            [
                $class: 'GitSCM',
                branches: [[name: branchName]],
                doGenerateSubmoduleConfigurations: false,
                extensions: [],
                gitTool: 'Default',
                submoduleCfg: [],
                userRemoteConfigs: [[credentialsId: 'GIT_CREDS', url: 'https://our-requried-git-repolink.git']]
            ]
        )
    }
}

