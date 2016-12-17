/**
 * Created by pault on 12/17/2016.
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
                        string(defaultValue: '', description: '', name: 'RELEASE_NOTES')
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
            buildDotnetApp {
                testProjects = config.testProjects
                publishProjects = config.publishProjects
                isRelease = IS_RELEASE
                releaseVersion = RELEASE_VERSION
            }
        }
    }
}