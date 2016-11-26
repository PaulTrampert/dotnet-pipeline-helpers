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

    properties([
            buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')),
            parameters([
                    booleanParam(defaultValue: false, description: '', name: 'IS_RELEASE'),
                    string(defaultValue: '', description: '', name: 'RELEASE_VERSION'),
                    text(defaultValue: '', description: '', name: 'RELEASE_NOTES')
            ]),
            pipelineTriggers([])
    ])

    def RELEASE_VERSION = params.RELEASE_VERSION
    def IS_RELEASE = params.IS_RELEASE
    def RELEASE_NOTES = params.RELEASE_NOTES

    echo "Building ${env.BRANCH_NAME}, commit ${env.CHANGE_ID}"

    node{
        stage("Update Sources") {
            checkout scm
        }

        buildNuget {
            project = config.project
            testProject = config.testProject
            isOpenSource = config.isOpenSource
            isRelease = IS_RELEASE
            releaseVersion = RELEASE_VERSION
            releaseNotes = RELEASE_NOTES
        }
    }
}
