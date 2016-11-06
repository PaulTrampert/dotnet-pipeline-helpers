def call(project, artifactDir, isRelease = false){
    stage("Package") {
        if (isRelease) {
            shell "dotnet pack ${project} --output ${artifactDir}"
        }
        else {
            def shortBranch = env.BRANCH_NAME.take(10)
            shell "dotnet pack ${project} --output ${artifactDir} --version-suffix ${shortBranch}-${env.BUILD_NUMBER}"
        }
    }
}