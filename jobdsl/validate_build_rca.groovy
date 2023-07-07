pipelineJob('validate_build_rca') {
    definition {
        cpsScm {
            lightweight(true)
            scm {
                git {
                    branch('main')
                    extensions {
                        pruneStaleBranch()
                        pruneTags {
                            pruneTags(true)
                        }
                    }
                    remote {
                        credentials('github')
                        url('https://github.com/aws-lumberyard/build-failure-rca.git')
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
        pipelineTriggers {
            triggers {
                pollSCM {
                    scmpoll_spec('TZ=America/Los_Angeles \nH/5  * * * * ')
                }
            }
        }
    }
}
