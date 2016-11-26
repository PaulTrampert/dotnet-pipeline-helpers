def call(project = '', args = []) {
    dotnet('restore')
    dotnet('build', project, args)
}