freeStyleJob('periodic-incremental-daily-development-trigger') {
    label('controller')
    properties {
        pipelineTriggers {
            triggers {
                cron {
                    spec('TZ=America/Los_Angeles \nH 22 * * *')
                }
            }
        }
    }
    publishers {
        downstream('O3DE-LY-Fork_periodic-incremental-daily-internal/development', 'FAILURE')
    }
}
