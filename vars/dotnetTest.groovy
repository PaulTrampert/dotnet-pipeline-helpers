def call(testProject, resultsFile) {
    stage("Test"){
        catchError{
            shell "dotnet test ${testProject} --result ${resultsFile}"
        }
    }
}