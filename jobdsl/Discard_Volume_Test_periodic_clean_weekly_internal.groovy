pipelineJob('Discard-Volume-Test_periodic-clean-weekly-internal') {
    definition {
        cpsScm {
            lightweight(true)
            scm {
                git {
                    branch('discard-volumes')
                    extensions {
                        pruneStaleBranch()
                        pruneTags {
                            pruneTags(true)
                        }
                    }
                    remote {
                        credentials('github')
                        url('https://github.com/aws-lumberyard-dev/o3de.git')
                    }
                }
            }
            scriptPath('scripts/build/Jenkins/Jenkinsfile')
        }
    }
    logRotator {
        daysToKeep(60)
        numToKeep(14)
    }
    properties {
        disableConcurrentBuilds {
            abortPrevious(false)
        }
        pipelineTriggers {
            triggers {
                cron {
                    spec('TZ=America/Los_Angeles \nH 21 * * 0 ')
                }
            }
        }
    }
}
