def call(project, args = []) {
    dotnet('pack', project, args)
}