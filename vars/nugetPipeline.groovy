def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    buildNuget {
        gitCredentialsId = config.gitCredentialsId
        gitRepoUrl = config.gitRepoUrl
        project = config.project
        notificationRecipients = config.notificationRecipients
    }

    currentBuild.result = "SUCCESS"

    input 'Deploy prerelease package?'

    deployNuget {
        nugetCredentialsId = config.nugetCredentialsId
        nugetServer = config.nugetServer
        notificationRecipients = config.notificationRecipients
    }
}