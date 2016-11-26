def call(String project = '', String[] args = []) {
    dotnet('restore')
    dotnet('build', project, args)
}