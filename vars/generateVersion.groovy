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
    echo "describeString = ${describeString}"
    def semver = SemVer.Parse describeString
    semver.minor++
    echo "semver.major = ${semver.major}"
    echo "semver.minor = ${semver.minor}"
    echo "semver.patch = ${semver.patch}"
    echo "semver.toString() = ${semver.toString()}"
    return semver.toString()
}