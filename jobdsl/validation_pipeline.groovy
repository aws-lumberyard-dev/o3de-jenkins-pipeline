pipelineJob('validation_pipeline') {
    definition {
        cpsScm {
            lightweight(true)
            scm {
                git {
                    branch('*/main')
                    extensions {
                        pruneStaleBranch()
                        pruneTags {
                            pruneTags(true)
                        }
                    }
                    remote {
                        url('https://git-codecommit.us-west-2.amazonaws.com/v1/repos/Validation')
                    }
                }
            }
            scriptPath('Jenkinsfile')
        }
    }
    logRotator {
        daysToKeep(7)
        numToKeep(200)
    }
    properties {
        disableConcurrentBuilds {
            abortPrevious(false)
        }
    }
}
