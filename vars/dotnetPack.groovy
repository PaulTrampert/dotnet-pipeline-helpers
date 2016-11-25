def call(String project, Map args = [:]) {
    dotnet('pack', project, args)
}