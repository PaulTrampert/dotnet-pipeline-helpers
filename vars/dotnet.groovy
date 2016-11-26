def call(String command, String project = '', String[] args = []) {
    def shellCommand = "dotnet ${command} ${project}"
    for (arg in args) {
        shellCommand = "${shellCommand} ${arg}"
    }
    shell shellCommand
}