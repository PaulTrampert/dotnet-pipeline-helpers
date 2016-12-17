/**
 * Created by pault on 12/17/2016.
 */
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

    def testProjects = config.testProjects
    def publishProjects = config.publishProjects
    def releaseVersion = config.releaseVersion

    try {

        stage("Build") {
            def buildArgs = []
            if (releaseVersion) {
                buildArgs << "/p:VersionPrefix=${releaseVersion}"
            }
            dotnetBuild('', buildArgs)
        }

        if (testProjects) {
            stage("Test") {
                for(def proj : testProjects) {
                    dotnetTest("${proj}/${proj}.csproj", ['--logger', 'trx', '--noBuild'])
                }
            }
        }

        if (publishProjects) {
            stage("Publish") {
                for(def proj : publishProjects) {
                    dotnetPublish("${proj}/${proj}.csproj", [])
                }
            }
        }

        stage("Reporting") {
            reportMSTestResults("**/*.trx")
            archiveArtifacts artifacts: "**/publish/**"
        }
    } catch (any) {
        currentBuild.result = "FAILURE"
        throw any
    } finally {
        deleteDir()
        emailext attachLog: true, recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    }
}