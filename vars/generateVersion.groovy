import com.ptrampert.SemVer

@NonCPS
def call() {
    String describeString
    if (isUnix()) {
        describeString = sh returnStdout: true, script: 'git describe --tags'
    }
    else {
        describeString = bat returnStdout: true, script: 'git describe --tags'
    }
    def semver = SemVer.Parse describeString
    semver.minor++
    return semver.toString()
}