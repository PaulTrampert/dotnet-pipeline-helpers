def call(String command, String project, Map args) {
    def shellCommand = "dotnet ${command} ${project}"
    args.keySet().each { shellCommand = "${shellCommand} ${it} ${args[it]}"}
    shell shellCommand
}