package com.ptrampert

def shell(command) {
    if (isUnix()) {
        sh command
    }
    else {
        bat command
    }
}

def build(project = "**/project.json", opts = "") {
    shell "dotnet build ${project} ${opts}"
}

def restore(project = "", opts = "") {
    shell "dotnet restore ${project} ${opts}"
}

def pack(project, opts = "") {
    shell "dotnet pack ${project} ${opts}"
}

def publish(project, opts = "") {
    shell "dotnet publish ${project} ${opts}"
}
