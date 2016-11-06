def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        try {
            unarchive()
            withCredentials([[$class: 'StringBinding', credentialsId: config.nugetCredentialsId, variable: 'NUGET_API_KEY']]) {
                shell 'nuget push Artifacts/*.nupkg -ApiKey ${env.NUGET_API_KEY} -Source ${config.nugetServer}'
            }
        } catch (any) {
            currentBuild.result = "FAILURE"
            throw any
        } finally {
            deleteDir()
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, sendToIndividuals: true, recipients: config.notificationRecipients])
        }
    }
}