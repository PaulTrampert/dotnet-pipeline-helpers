def call(String command, String project, Map args) {
    def shellCommand = "dotnet ${command} ${project}"
    args.keySet().each {k -> shellCommand = "${shellCommand} ${k} ${args[k]}"}
    shell shellCommand
}