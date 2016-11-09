package com.ptrampert.dotnet

class Dotnet {
    def shell

    Dotnet(shell) {
        this.shell = shell
    }

    def build(String project = '**/project.json', Map args = [:]) {
        command('build', project, args)
    }

    def restore() {
        shell.exec "dotnet restore"
    }

    def test(String project, Map args) {
        command('test', project, args)
    }

    def pack(String project, Map args) {
        command('pack', project, args)
    }

    def publish(String project, Map args) {
        command('publish', project, args)
    }

    def command(String command, String project, Map args) {
        def shellCommand = "dotnet ${command} ${project}"
        args.each {k, v -> shellCommand = "${shellCommand} ${k} ${v}"}
        shell.exec shellCommand
    }
}