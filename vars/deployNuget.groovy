def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    try {
        stage("Publish Package") {
            unstash 'nupkg'
            def nugetCredentialsId = config.nugetCredentialsId
            def nugetServer = config.nugetServer
            def symbolCredId = config.symbolsCredentialsId ?: nugetCredentialsId
            def symbolServer = config.symbolServer
            echo "Credentials ID: ${config.nugetCredentialsId ?: '<default>'}"
            echo "Nuget Server: ${config.nugetServer ?: '<default>'}"
            echo "Symbols Credentials ID: ${symbolCredId ?: '<default>'}"
            echo "Symbol Server: ${symbolServer ?: '<default>'}"

            def args = []
            if (nugetCredentialsId) {
                withCredentials([string(credentialsId: nugetCredentialsId, variable: 'NUGET_API_KEY')]) {
                    args << "--api-key ${env.NUGET_API_KEY}"
                }
            }
            if (nugetServer) {
                args << "--source ${nugetServer}"
            }
            if (symbolCredId) {
                withCredentials([string(credentialsId: nugetCredentialsId, variable: 'SYMBOL_API_KEY')]) {
                    args << "--symbol-api-key ${env.SYMBOL_API_KEY}"
                }
            }
            if (symbolServer) {
                args << "--symbol-source ${symbolServer}"
            }
            if (!config.openSource) {
                args << "--no-symbols"
            }
            dotnetNugetPush("**/*.nupkg", args)
        }
    } catch (any) {
        currentBuild.result = "FAILURE"
        throw any
    } finally {
        deleteDir()
        emailext attachLog: true, recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    }
}