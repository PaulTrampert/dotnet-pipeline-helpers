/**
* Configures a full nuget build, test, and deployment pipeline. Assumes git as the source control.
* Build Parameters:
*   ReleaseVersion: The release version. This will be made available to any build scripts via an environment variable of the same name.
*   NextVersion: The next working version. This will be made available to any build scripts via an environment variable of the same name.
*   IsRelease: Indicates whether to build a release package or a pre-release package. 
* Config Values:
*   project: The project to Build
*   testProject: The test project for running tests. Defaults to "${project}.Test"
*   artifactDir: The directory to write artifacts out to. Defaults to "Artifacts"
*/
def call(body) {
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    properties([
        parameters([
            string(defaultValue: '', description: '', name: 'ReleaseVersion'), 
            string(defaultValue: '', description: '', name: 'NextVersion'), 
            booleanParam(defaultValue: false, description: '', name: 'IsRelease')
        ]), 
        pipelineTriggers([])
    ])
    properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '5')), pipelineTriggers([])])

    env.ReleaseVersion = params.ReleaseVersion
    env.NextVersion = params.NextVersion

    echo params.ReleaseVersion
    echo params.NextVersion
    echo params.IsRelease

    buildNuget {
        project = config.project
        notificationRecipients = config.notificationRecipients
        artifactDir = config.artifactDir
        isRelease = params.IsRelease
    }
}
