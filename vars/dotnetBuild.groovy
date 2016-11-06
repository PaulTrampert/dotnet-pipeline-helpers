def call() {

    stage("Build") {
        if (env.NugetConfig) {
            shell "dotnet restore --configfile ${env.NugetConfig}"
        }
        else {
            shell "dotnet restore"
        }
        shell "dotnet build **/project.json"
    }
}