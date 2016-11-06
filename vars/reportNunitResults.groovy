def call(resultsPattern) {
    def nunitXslt = libraryResource "com/ptrampert/dotnet/nunit3-xunit.xslt"
    writeFile file: 'nunit3-xunit.xslt', text: nunitXslt
    step([
            $class        : 'XUnitBuilder',
            testTimeMargin: '3000',
            thresholdMode : 1,
            thresholds    : [
                    [$class: 'FailedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '0'],
                    [$class: 'SkippedThreshold', failureNewThreshold: '', failureThreshold: '', unstableNewThreshold: '', unstableThreshold: '']],
            tools         : [
                    [$class: 'CustomType', customXSL: 'nunit3-xunit.xslt', deleteOutputFiles: true, failIfNotNew: true, pattern: resultsPattern, skipNoTestFiles: false, stopProcessingIfError: true]]
    ])
}