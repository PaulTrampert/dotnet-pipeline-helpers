def shell(GString command) {
    if (isUnix()) {
        sh command
    }
    else {
        bat command
    }
}

def build(String project, String opts) {
    if (!project) {
        project = "**/project.json"
    }

    shell "dotnet build ${project} ${opts}"
}

def restore(String project, String opts) {
    shell "dotnet restore ${project} ${opts}"
}

def pack(String project, String opts) {
    shell "dotnet pack ${project} ${opts}"
}

def publish(String project, String opts) {
    shell "dotnet publish ${project} ${opts}"
}