/**
* Configures a full nuget build, test, and deployment pipeline. Assumes git as the source control.
* Config Values:
*   project: The project to Build
*   artifactDir: The directory to write artifacts out to. Defaults to "Artifacts"
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
            if (!fileExists(artifactDir)) {
                shell "mkdir ${artifactDir}"
            }
        }

        buildNuget {
            project = config.project
            artifactDir = config.artifactDir
            isRelease = false
        }
    }
}
