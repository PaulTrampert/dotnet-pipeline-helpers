def call(String command, String project, Map args) {
    def shellCommand = "dotnet ${command} ${project}"
    for (k in args.keySet()) {
        shellCommand = "${shellCommand} ${k} ${args[k] ?: ''}"
    }
    shell shellCommand
}