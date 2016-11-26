def call(String command, String project, Map args) {
    def shellCommand = "dotnet ${command} ${project}"
    echo "Command Line Arg Keys: ${args.keySet()}"
    for (k in args.keySet()) {
        echo "Cmd Line Arg: ${k} ${args[k]}"
        shellCommand = "${shellCommand} ${k} ${args[k] == true ? '' : args[k]}"
    }
    shell shellCommand
}