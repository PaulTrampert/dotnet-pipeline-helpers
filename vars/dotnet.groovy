def call(String command, String project, Map args) {
    def shellCommand = "dotnet ${command} ${project}"
    echo "Command Line Arg Keys: ${args.keySet()}"
    args.keySet().each {
        echo "Cmd Line Arg: ${it} ${args[it]}"
        shellCommand = "${shellCommand} ${it} ${args[it] == true ? '' : args[it]}"
    }
    shell shellCommand
}