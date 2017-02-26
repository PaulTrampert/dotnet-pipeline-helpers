def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    try {
        stage("Publish Package") {
            def deployNupkg = config.deployNupkg
            def nugetCredentialsId = config.nugetCredentialsId
            def nugetServer = config.nugetServer ?: 'https://api.nuget.org/v3/index.json'
            def deploySymbols = config.deploySymbols
            def symbolCredId = config.symbolsCredentialsId ?: nugetCredentialsId
            def symbolServer = config.symbolServer
            echo "Credentials ID: ${nugetCredentialsId ?: '<default>'}"
            echo "Nuget Server: ${nugetServer ?: '<default>'}"
            echo "Symbols Credentials ID: ${symbolCredId ?: '<default>'}"
            echo "Symbol Server: ${symbolServer ?: '<none>'}"

            if (deployNupkg && nugetServer && nugetCredentialsId) {
                withCredentials([string(credentialsId: nugetCredentialsId, variable: 'NUGET_API_KEY')]) {
                    unstash "nupkg"
                    def args = []
                    args << "--api-key ${env.NUGET_API_KEY}"
                    args << "--source ${nugetServer}"
                    dotnetNugetPush("**/*.nupkg", args)
                    deleteDir()
                }
            }
            if (deploySymbols && symbolServer && symbolCredId) {
                withCredentials([string(credentialsId: symbolCredId, variable: "SYMBOL_API_KEY")]) {
                    unstash "symbols"
                    def args = []
                    args << "--api-key ${env.SYMBOL_API_KEY}"
                    args << "--source ${symbolServer}"
                    dotnetNugetPush("**/*.symbols.nupkg", args)
                    deleteDir()
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