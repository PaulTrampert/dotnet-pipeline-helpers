import com.ptrampert.dotnet.Dotnet
import com.ptrampert.util.Shell

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

    def project = "${config.project}/${config.project}.csproj"
    def testProject = "${config.testProject}/${config.testProject}.csproj" ?: "${config.project}.Test/${config.project}.Test.csproj"
    def isRelease = config.isRelease

    def dotnet = new Dotnet(new Shell(steps))

    try {

        stage("Build") {
            dotnet.restore()
            dotnet.build()
        }

        stage("Test") {
            dotnet.test(testProject, ['--logger': 'trx'])
        }

        stage("Package") {
            def packArgs = [:]
            if (!isRelease) {
                packArgs.put('--version-suffix', "${env.BRANCH_NAME.take(10)}-${env.BUILD_NUMBER}")
            }
            dotnet.pack(project, packArgs)
        }
        
        stage("Reporting") {
            reportMSTestResults("**/*.trx")
            archiveArtifacts artifacts: "**/*.nupkg", excludes: "**/*.symbols.nupkg"
            stash excludes: "**/*.symbols.nupkg", includes: "**/*.nupkg", name: "nupkg"
        }
    } catch (any) {
        currentBuild.result = "FAILURE"
        throw any
    } finally {
        deleteDir()
        emailext attachLog: true, recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    }
}