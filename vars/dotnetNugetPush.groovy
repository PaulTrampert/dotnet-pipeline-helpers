def call(project = '**/*.nupkg', args = []) {
    dotnet('nuget push', project, args)
}