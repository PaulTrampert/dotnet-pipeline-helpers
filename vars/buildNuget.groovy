def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    node {
        try {
            stage("Update Sources") {
                checkout scm
            }

            stage("Build") {
                if (!fileExists('Artifacts')) {
                    shell "mkdir Artifacts"
                }
                dotnetBuild()
            }

            stage("Test") {
                catchError {
                    shell "dotnet test ${config.project}.Test --result Artifacts/TestResults.xml"
                }
            }

            stage("Package") {
                def shortBranch = env.BRANCH_NAME.take(10)
                shell "dotnet pack ${config.project} --output Artifacts --version-suffix ${shortBranch}-${env.BUILD_NUMBER}"
            }

            stage("Reporting") {
                def nunitXslt = libraryResource "com/ptrampert/dotnet/nunit3-xunit.xslt"
                writeFile file: 'nunit3-xunit.xslt', text: nunitXslt
                step([
                        $class        : 'XUnitBuilder',
                        testTimeMargin: '3000',
                        thresholdMode : 1,
                        thresholds    : [
                                [$class: 'FailedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '0'],
                                [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']],
                        tools         : [
                                [$class: 'CustomType', customXSL: 'nunit3-xunit.xslt', deleteOutputFiles: true, failIfNotNew: true, pattern: 'Artifacts/TestResults.xml', skipNoTestFiles: false, stopProcessingIfError: true]]
                ])
                archiveArtifacts artifacts: 'Artifacts/*.nupkg', excludes: 'Artifacts/*.symbols.nupkg'
                stash excludes: 'Artifacts/*.symbols.nupkg', includes: 'Artifacts/*.nupkg', name: 'nupkg'
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