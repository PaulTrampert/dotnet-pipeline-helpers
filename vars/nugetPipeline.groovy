/**
* Configures a full nuget build, test, and deployment pipeline. Assumes git as the source control.
* Config Values:
*   project: The project to Build
*   artifactDir: The directory to write artifacts out to. Defaults to "Artifacts"
*   notificationRecipients: Emails to notify on failure.
*/
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), pipelineTriggers([])])

    def project = config.project
    def artifactDir = config.artifactDir
    node{
        stage("Update Sources") {
            checkout scm
            if (!fileExists(artifactDir)) {
                shell "mkdir ${artifactDir}"
            }
        }

        buildNuget {
            project = project
            artifactDir = artifactDir
            isRelease = false
        }
    }
}
