/**
* Configures a full nuget build, test, and deployment pipeline. Assumes git as the source control.
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

    buildNuget {
        project = config.project
        notificationRecipients = config.notificationRecipients
        artifactDir = config.artifactDir
        isRelease = config.isRelease
    }
}
