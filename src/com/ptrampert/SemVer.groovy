package com.ptrampert

/**
 * Created by pault on 2/26/2017.
 */
class SemVer {
    int major
    int minor
    int patch

    static SemVer Parse(String str) {
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
        return "${major}.${minor}.${patch}"
    }
}
