package com.ptrampert

def shell(GString command) {
    if (isUnix()) {
        sh command
    }
    else {
        bat command
    }
}

def build(project, opts) {
    if (!project) {
        project = "**/project.json"
    }

    shell "dotnet build ${project} ${opts}"
}

def restore(project, opts) {
    shell "dotnet restore ${project} ${opts}"
}

def pack(project, opts) {
    shell "dotnet pack ${project} ${opts}"
}

def publish(project, opts) {
    shell "dotnet publish ${project} ${opts}"
}
