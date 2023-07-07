pipelineJob('O3DE-LY-Fork-development_weekly-artifacts-upload') {
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
    displayName('O3DE Fork [Periodic Clean Weekly] Development')
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
