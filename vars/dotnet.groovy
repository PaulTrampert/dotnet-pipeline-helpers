def call(command, project = '', args = []) {
    def shellCommand = "dotnet ${command} ${project}"
    for (arg in args) {
        shellCommand = "${shellCommand} ${arg}"
    }
    shell shellCommand
}