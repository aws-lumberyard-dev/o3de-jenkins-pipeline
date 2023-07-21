pipelineJob('O3DE-LY-Fork-development_periodic-clean-weekly-internal') {
    definition {
        cpsScm {
            lightweight(true)
            scm {
                git {
                    branch('development')
                    extensions {
                        pruneStaleBranch()
                        pruneTags {
                            pruneTags(true)
                        }
                    }
                    remote {
                        credentials('github')
                        name('')
                        refspec('')
                        url('https://github.com/aws-lumberyard-dev/o3de.git')
                    }
                }
            }
            scriptPath('scripts/build/Jenkins/Jenkinsfile')
        }
    }
    displayName('O3DE LY-Fork [Periodic Weekly Clean] Development')
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
                    spec('H 21 * * 0')
                }
            }
        }
    }
}
