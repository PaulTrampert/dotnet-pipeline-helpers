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

    if (env.BRANCH_NAME) {
        properties([
                buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5'))
        ])
    }
    else {
        properties([
                buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')),
                parameters([
                        booleanParam(defaultValue: true, description: '', name: 'IS_RELEASE'),
                        string(defaultValue: '', description: '', name: 'RELEASE_VERSION'),
                        text(defaultValue: '', description: '', name: 'RELEASE_NOTES')
                ]),
                pipelineTriggers([])
        ])
    }


    def RELEASE_VERSION = params.RELEASE_VERSION
    def RELEASE_NOTES = params.RELEASE_NOTES
    def IS_RELEASE = params.IS_RELEASE

    echo "Building ${env.BRANCH_NAME}"

    node{
        stage("Update Sources") {
            checkout scm
        }
        withEnv(["PackageReleaseNotes=\"${RELEASE_NOTES}\""]) {
            buildNuget {
                project = config.project
                packProjects = config.packProjects
                testProject = config.testProject
                testProjects = config.testProjects
                publishProjects = config.publishProjects
                isOpenSource = config.isOpenSource
                isRelease = IS_RELEASE
                releaseVersion = RELEASE_VERSION
            }
        }

        if (IS_RELEASE) {
            echo "Credentials ID: ${config.nugetCredentialsId}"
            echo "Nuget Server: ${config.nugetServer}"
            deployNuget {
                nugetCredentialsId = config.nugetCredentialsId
                nugetServer = config.nugetServer
                symbolCredentialsId = config.symbolsCredentialsId
                symbolServer = config.symbolServer
                isOpenSource = config.isOpenSource
            }
        }
    }
}
