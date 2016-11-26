def call(String project, String[] args = []) {
    dotnet('test', project, args)
}