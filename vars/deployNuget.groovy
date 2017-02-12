def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    try {
        stage("Publish Package") {
            unstash 'nupkg'
            def nugetCredentialsId = config.nugetCredentialsId
            def nugetServer = config.nugetServer ?: 'https://api.nuget.org/v3/index.json'
            def symbolCredId = config.symbolsCredentialsId ?: nugetCredentialsId
            def symbolServer = config.symbolServer
            echo "Credentials ID: ${config.nugetCredentialsId ?: '<default>'}"
            echo "Nuget Server: ${config.nugetServer ?: '<default>'}"
            echo "Symbols Credentials ID: ${symbolCredId ?: '<default>'}"
            echo "Symbol Server: ${symbolServer ?: '<default>'}"

            withCredentials([string(credentialsId: nugetCredentialsId, variable: 'NUGET_API_KEY')]) {
                withCredentials([string(credentialsId: symbolCredId, variable: 'SYMBOL_API_KEY')]) {
                    def args = []
                    args << "--api-key ${env.NUGET_API_KEY}"
                    args << "--source ${nugetServer}"
                    if (config.isOpenSource) {
                        args << "--symbol-api-key ${env.SYMBOL_API_KEY}"
                    }
                    if (symbolServer && config.isOpenSource) {
                        args << "--symbol-source ${symbolServer}"
                    }
                    if (!config.isOpenSource) {
                        args << "--no-symbols true"
                    }
                    dotnetNugetPush("**/*.nupkg", args)
                }
            }
        }
    } catch (any) {
        currentBuild.result = "FAILURE"
        throw any
    } finally {
        deleteDir()
        emailext attachLog: true, recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    }
}