def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        try {
            stage("Update Sources") {
                if (config.credentialsId) {
                    git url: config.repoUrl, credentialsId: config.credentialsId
                } else {
                    git url: config.repoUrl
                }
                shell "git clean -xdf"
            }

            stage("Build") {
                if (!fileExists('Artifacts')) {
                    shell "mkdir Artifacts"
                }
                if (env.NugetConfig) {
                    shell "dotnet restore -c ${env.NugetConfig}"
                }
                else {
                    shell "dotnet restore"
                }
                shell "dotnet build **/project.json"
            }

            stage("Test") {
                catchError {
                    shell "dotnet test ${config.project}.Test --result Artifacts/TestResults.xml"
                }
            }

            stage("Package") {
                def shortBranch = env.BRANCH_NAME.take(10)
                shell "dotnet pack ${config.project} --output Artifacts --version-suffix ${shortBranch}.${env.BUILD_NUMBER}"
            }

            stage("Reporting") {
                step([
                        $class        : 'XUnitBuilder',
                        testTimeMargin: '3000',
                        thresholdMode : 1,
                        thresholds    : [
                                [$class: 'FailedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '0'],
                                [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']],
                        tools         : [
                                [$class: 'CustomType', customXSL: 'C:\\Jenkins\\userContent\\nunit3-xunit.xslt', deleteOutputFiles: true, failIfNotNew: true, pattern: 'Artifacts/TestResults.xml', skipNoTestFiles: false, stopProcessingIfError: true]]
                ])
                archiveArtifacts 'Artifacts/**'
            }
        } catch (any) {
            currentBuild.result = "FAILURE"
            throw any
        } finally {
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, sendToIndividuals: true, recipients: config.notificationRecipients])
        }
    }
}