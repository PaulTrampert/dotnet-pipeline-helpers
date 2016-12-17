def call(project, args = []) {
    dotnet('publish', project, args)
}