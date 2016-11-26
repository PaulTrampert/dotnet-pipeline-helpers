def call(project, args = []) {
    dotnet('test', project, args)
}