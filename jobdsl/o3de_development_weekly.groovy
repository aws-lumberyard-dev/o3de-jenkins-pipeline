pipelineJob('O3DE-Fork_weekly') {
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
                        url('https://github.com/aws-lumberyard-dev/o3de.git')
                    }
                }
            }
            scriptPath('scripts/build/Jenkins/Jenkinsfile')
        }
    }
    displayName('O3DE Fork [Periodic Weekly] Development')
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
                    spec('TZ=America/Los_Angeles \nH 0 * * 6')
                }
            }
        }
    }
}
