package com.ptrampert.util

/**
 * Created by pault on 11/8/2016.
 */
class Shell {
    def steps

    Shell(steps) {
        this.steps = steps
    }

    def exec(command) {
        if (steps.isUnix()) {
            steps.sh command
        }
        else {
            steps.bat command
        }
    }
}
