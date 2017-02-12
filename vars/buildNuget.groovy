/**
* Builds and tests a nuget package. Assumes nunit3 as the test runner.
* Config Values:
*   project: The project to Build
*   testProject: The test project for running tests. Defaults to '${project}.Test'
*   isRelease: True if performing a release build. False if pre-release.
*/
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def project = config.project ? "${config.project}/${config.project}.csproj" : null
    def packProjects = config.packProjects
    def testProject = config.testProject ? "${config.testProject}/${config.testProject}.csproj" : (config.project ? "${config.project}.Test/${config.project}.Test.csproj" : null)
    def testProjects = config.testProjects
    def isRelease = config.isRelease
    def releaseVersion = config.releaseVersion
    def isOpenSource = config.isOpenSource

    try {

        stage("Build") {
            def buildArgs = []
            if (!isRelease && env.BRANCH_NAME) {
                buildArgs << "--version-suffix ${env.BRANCH_NAME.take(10)}-${env.BUILD_NUMBER}"
            }
            if (releaseVersion) {
                buildArgs << "/p:VersionPrefix=${releaseVersion}"
            }
            dotnetBuild('', buildArgs)
        }

        if (testProject) {
            stage("Test") {
                dotnetTest(testProject, ['--logger', 'trx', '--no-build'])
            }
        }

        if (testProjects) {
            stage("Test") {
                for(def proj : testProjects) {
                    dotnetTest("${proj}/${proj}.csproj", ['--logger', 'trx', '--no-build'])
                }
            }
        }

        if (project) {
            stage("Package") {
                def packArgs = ['--no-build']
                if (!isRelease && env.BRANCH_NAME) {
                    packArgs << "--version-suffix ${env.BRANCH_NAME.take(10)}-${env.BUILD_NUMBER}"
                }
                if (isOpenSource) {
                    packArgs << '--include-source'
                }
                if (releaseVersion) {
                    packArgs << "/p:VersionPrefix=${releaseVersion}"
                }
                dotnetPack(project, packArgs)
            }
        }

        if (packProjects) {
            stage("Package") {
                echo "looping through ${packProjects}"
                for(def proj : packProjects) {
                    def packArgs = ['--no-build']
                    if (!isRelease) {
                        packArgs << "--version-suffix ${env.BRANCH_NAME.take(10)}-${env.BUILD_NUMBER}"
                    }
                    if (isOpenSource) {
                        packArgs << '--include-source'
                    }
                    if (releaseVersion) {
                        packArgs << "/p:VersionPrefix=${releaseVersion}"
                    }
                    dotnetPack("${proj}/${proj}.csproj", packArgs)
                }
            }
        }
        
        stage("Reporting") {
            reportMSTestResults("**/*.trx")
            archiveArtifacts artifacts: "**/*.nupkg"
            stash includes: "**/*.nupkg", name: "nupkg"
        }
    } catch (any) {
        currentBuild.result = "FAILURE"
        throw any
    } finally {
        deleteDir()
        emailext attachLog: true, recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    }
}
