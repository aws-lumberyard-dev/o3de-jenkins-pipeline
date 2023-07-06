pipelineJob('projects_periodic_trigger') {
    definition {
        cps {
            sandbox(true)
            script('''
                build job: 'o3de-multiplayersample_periodic-clean-daily-internal/development', 
                    wait: false,
                    parameters:[
                        booleanParam(name: 'UPLOAD_BUILD_ARTIFACTS', value: true),
                        booleanParam(name: 'CLEAN_WORKSPACE', value: true),
                        string(name: 'S3SIS_UPLOAD_PARAMS', value: '--include "build/**/*.*"')
                    ]

                build job: 'o3de-atom-sampleviewer_periodic-clean-daily-internal/development', 
                    wait: false,
                    parameters:[
                        booleanParam(name: 'UPLOAD_BUILD_ARTIFACTS', value: true),
                        booleanParam(name: 'CLEAN_WORKSPACE', value: true),
                        string(name: 'S3SIS_UPLOAD_PARAMS', value: '--include "build/**/*.*"')
                    ]
            '''.stripIndent().trim())
        }
    }
    logRotator {
        daysToKeep(7)
        numToKeep(14)
    }
    properties {
        pipelineTriggers {
            triggers {
                cron {
                    spec('TZ=America/Los_Angeles \nH 22 * * * ')
                }
            }
        }
    }
}
