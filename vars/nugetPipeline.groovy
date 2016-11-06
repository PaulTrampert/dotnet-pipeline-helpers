def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    buildNuget {
        credentialsId = config.gitCredentialsId
        repoUrl = config.gitRepoUrl
        project = config.project
        notificationRecipients = config.notificationRecipients
    }
}