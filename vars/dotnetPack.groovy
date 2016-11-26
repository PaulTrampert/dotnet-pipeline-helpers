def call(String project, String[] args = []) {
    dotnet('pack', project, args)
}