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

            dotnetBuild()

            dotnetTest(testProject, "${artifactDir}/TestResults.xml")

            dotnetPack(project, artifactDir, isRelease)

            stage("Reporting") {
                reportNunitResults("${artifactDir}/TestResults.xml")
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