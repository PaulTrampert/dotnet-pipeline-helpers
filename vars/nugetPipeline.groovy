/**
* Configures a full nuget build, test, and deployment pipeline. Assumes git as the source control.
* Config Values:
*   project: The project to Build
*/
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), pipelineTriggers([])])

    config.artifactDir = config.artifactDir ?: "Artifacts"

    node{
        stage("Update Sources") {
            checkout scm
        }

        buildNuget {
            project = config.project
            testProject = config.testProject
            isRelease = false
        }
    }
}
