import com.ptrampert.SemVer

def call() {
    String describeString
    if (isUnix()) {
        describeString = sh returnStdout: true, script: 'git describe --tags'
    }
    else {
        describeString = bat returnStdout: true, script: 'git describe --tags'
    }
    echo "describeString = ${describeString}"
    def result = calculateSemver describeString
    echo "semver.toString() = ${result}"
    return result
}

@NonCPS
def calculateSemver(str) {
    def semver = SemVer.Parse describeString
    semver.minor++
    return semver.toString()
}