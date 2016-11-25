def call(String project = '', Map args = [:]) {
    dotnet('restore', project, args)
    dotnet('build', project, args)
}