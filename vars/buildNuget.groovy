/**
* Builds and tests a nuget package. Assumes nunit3 as the test runner.
* Config Values:
*   project: The project to Build
*   testProject: The test project for running tests. Defaults to '${project}.Test'
*   artifactDir: The directory to write artifacts out to. Defaults to 'Artifacts'
*   isRelease: True if performing a release build. False if pre-release. 
*/
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def project = config.project
    def testProject = config.testProject ? config.testProject : "${project}.Test"
    def artifactDir = config.artifactDir ? config.artifactDir : "Artifacts"
    def isRelease = config.isRelease

    node {
        try {
            stage("Update Sources") {
                checkout scm
                if (!fileExists(artifactDir)) {
                    shell "mkdir ${artifactDir}"
                }
            }

            stage("Build") {
                dotnetBuild()
            }

            stage("Test") {
                catchError {
                    shell "dotnet test ${testProject} --result ${artifactDir}/TestResults.xml"
                }
            }

            stage("Package") {
                if (isRelease) {
                    shell "dotnet pack ${project} --output ${artifactDir}"
                }
                else {
                    def shortBranch = env.BRANCH_NAME.take(10)
                    shell "dotnet pack ${project} --output ${artifactDir} --version-suffix ${shortBranch}-${env.BUILD_NUMBER}"
                }
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
                                [$class: 'CustomType', customXSL: 'nunit3-xunit.xslt', deleteOutputFiles: true, failIfNotNew: true, pattern: "${artifactDir}/TestResults.xml", skipNoTestFiles: false, stopProcessingIfError: true]]
                ])
                archiveArtifacts artifacts: "${artifactDir}/*.nupkg", excludes: "${artifactDir}/*.symbols.nupkg"
                stash excludes: "${artifactDir}/*.symbols.nupkg", includes: "${artifactDir}/*.nupkg", name: "nupkg"
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