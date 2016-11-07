/**
* Configures a full nuget build, test, and deployment pipeline. Assumes git as the source control.
* Config Values:
*   project: The project to Build
*   artifactDir: The directory to write artifacts out to. Defaults to "Artifacts"
*   releaseApprover: Email address of the person who can approve a release.
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
            if (!fileExists(config.artifactDir)) {
                shell "mkdir ${config.artifactDir}"
            }
        }

        buildNuget {
            project = config.project
            artifactDir = config.artifactDir
            isRelease = false
        }
    }

    if (env.BRANCH_NAME == 'master') {
        def releaseParams
        stage("Release Approval") {
            timeout(time: 1, unit: 'DAYS') {
                
                mail to: config.releaseApprover,
                    subject: "Pending Action on ${env.JOB_BASE_NAME} Build ${env.BUILD_NUMBER}"
                    body: "Please review ${env.BUILD_URL} for release."
                
                releaseParams = input 
                    message: 'Proceed with release?', 
                    parameters: [
                        string(defaultValue: '', description: '', name: 'ReleaseVersion')
                    ]
            }
        }

        env.ReleaseVersion = releaseParams.ReleaseVersion
        env.NextVersion = releaseParams.NextVersion

        node {
            stage("Update Release Sources") {
                checkout scm
                if (!fileExists(config.artifactDir)) {
                    shell "mkdir ${config.artifactDir}"
                }
                shell "git tag ${env.ReleaseVersion}"
            }

            buildNuget {
                project = config.project
                artifactDir = config.artifactDir
                isRelease = true
            }

            deployNuget {
                nugetCredentialsId = config.nugetCredentialsId
                nugetServer = config.nugetServer
            }

            stage("Commit Tag") {
                shell "git push origin ${env.ReleaseVersion}"
            }
        }
    }
}
