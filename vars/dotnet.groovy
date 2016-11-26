def call(String command, String project, Map args) {
    def shellCommand = "dotnet ${command} ${project}"
    echo "${args.keySet()}"
    args.each {k, v -> shellCommand = "${shellCommand} ${k} ${v}"}
    shell shellCommand
}