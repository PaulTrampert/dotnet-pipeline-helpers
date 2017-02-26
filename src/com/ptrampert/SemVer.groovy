package com.ptrampert

class SemVer implements Serializable {
    private static final long serialVersionUID = 1234L

    int major
    int minor
    int patch

    static SemVer Parse(str) {
        def matcher = (str =~ ~/(\d+)\.(\d+)\.(\d+)/)
        def result = new SemVer()
        if (matcher.matches()) {
            throw new Exception("Cannot parse ${str} as SemVer")
        }
        result.major = Integer.parseInt(matcher[0][1])
        result.minor = Integer.parseInt(matcher[0][2])
        result.patch = Integer.parseInt(matcher[0][3])
        return result
    }

    String toString() {
        return "${major}.${minor}.${patch}".toString()
    }
}
