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
    properties([
            parameters([
                    booleanParam(defaultValue: false, description: '', name: 'IS_RELEASE'),
                    string(defaultValue: '', description: '', name: 'RELEASE_VERSION')
            ]),
            pipelineTriggers([])
    ])

    node{
        stage("Update Sources") {
            checkout scm
        }

        buildNuget {
            project = config.project
            testProject = config.testProject
            isOpenSource = config.isOpenSource
            isRelease = params ? false : params.IS_RELEASE
            releaseVersion = params ? null : params.RELEASE_VERSION
        }
    }
}
