def call(command) {
    if (isUnix()) {
        sh command
    }
    else {
        bat command
    }
}
