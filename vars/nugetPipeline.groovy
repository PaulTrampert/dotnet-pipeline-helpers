def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    buildNuget {
        project = config.project
        notificationRecipients = config.notificationRecipients
    }
}
