freeStyleJob('sync_o3de_main') {
    label('sync-fork')
    parameters {
        stringParam('UPSTREAM', 'https://github.com/o3de/o3de.git', 'Updates will be fetched from this repo.')
        stringParam('ORIGIN', 'https://github.com/aws-lumberyard-dev/o3de.git', 'Target repo for the sync. Changes will be pushed to this repo.')
        stringParam('BRANCH', 'main', '')
        stringParam('PARAMETER', '/github/aws-lumberyard-dev', 'Parameter store with the GitHub credentials')
        stringParam('REGION', 'us-west-2', '')
    }
    properties {
        pipelineTriggers {
            triggers {
                pollSCM {
                    scmpoll_spec('H/5 * * * *')
                }
            }
        }
    }
    publishers {
        mailer('ly-infra@amazon.com', false, false)
    }
    scm {
        git {
            branch('main')
            remote {
                credentials('github')
                name('')
                refspec('')
                url('https://github.com/o3de/o3de.git')
            }
        }
    }
    steps {
        shell('''
            cd ..
            git config --global checkout.defaultRemote origin
            python3 $WORKSPACE/scripts/build/tools/sync_repo.py $UPSTREAM $ORIGIN -b $BRANCH -w . -p $PARAMETER -r $REGION     
        '''.stripIndent().trim())
    }
}
