import com.ptrampert.dotnet.Dotnet
import com.ptrampert.util.Shell

/**
* Builds and tests a nuget package. Assumes nunit3 as the test runner.
* Config Values:
*   project: The project to Build
*   testProject: The test project for running tests. Defaults to '${project}.Test'
*   artifactDir: The directory to write artifacts out to. Defaults to 'Artifacts'
*   isRelease: True if performing a release build. False if pre-release. 
*/
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    def project = config.project
    def testProject = config.testProject ?: "${project}.Test"
    def artifactDir = config.artifactDir ?: "Artifacts"
    def isRelease = config.isRelease

    def dotnet = new Dotnet(new Shell(steps))

    try {

        stage("Build") {
            dotnet.restore()
            dotnet.build()
        }

        stage("Test") {
            dotnet.test(testProject, ['--result': "${artifactDir}/TestResults.xml"])
        }

        stage("Package") {
            def packArgs = ['--output': artifactDir]
            if (isRelease) {
                packArgs.put('--version-suffix', "${env.BRANCH_NAME.take(10)}-${env.BUILD_NUMBER}")
            }
            dotnet.pack(project, packArgs)
        }
        
        stage("Reporting") {
            reportNunitResults("${artifactDir}/TestResults.xml")
            archiveArtifacts artifacts: "${artifactDir}/*.nupkg", excludes: "${artifactDir}/*.symbols.nupkg"
            stash excludes: "${artifactDir}/*.symbols.nupkg", includes: "${artifactDir}/*.nupkg", name: "nupkg"
        }
    } catch (any) {
        currentBuild.result = "FAILURE"
        throw any
    } finally {
        deleteDir()
        emailext attachLog: true, recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    }
}