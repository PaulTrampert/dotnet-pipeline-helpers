def call(String project, Map args = [:]) {
    dotnet('test', project, args)
}