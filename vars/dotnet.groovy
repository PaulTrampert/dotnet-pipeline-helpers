def call(String command, String project, Map args) {
    def shellCommand = "dotnet ${command} ${project}"
    args.each {k, v -> shellCommand = "${shellCommand} ${k} ${v}"}
    shell shellCommand
}