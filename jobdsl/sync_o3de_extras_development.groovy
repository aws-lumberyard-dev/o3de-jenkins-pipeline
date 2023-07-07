freeStyleJob('sync_o3de-extras_development') {
    label('sync-fork')
    parameters {
        stringParam('UPSTREAM', 'https://github.com/o3de/o3de-extras.git', 'Updates will be fetched from this repo.')
        stringParam('ORIGIN', 'https://github.com/aws-lumberyard-dev/o3de-extras.git', 'Target repo for the sync. Changes will be pushed to this repo.')
        stringParam('BRANCH', 'development', '')
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
            branch('development')
            remote {
                credentials('github')
                url('https://github.com/o3de/o3de-extras.git')
            }
        }
    }
    steps {
        shell('''
            cd ..
            git config --global checkout.defaultRemote origin
            mkdir o3de && cd o3de
            git init
            git remote add origin https://github.com/o3de/o3de.git
            git sparse-checkout init --cone
            git sparse-checkout set scripts/build/tools/
            git fetch --depth=1 origin main
            git checkout main

            cd ..
            git config --global checkout.defaultRemote origin
            python3 o3de/scripts/build/tools/sync_repo.py $UPSTREAM $ORIGIN -b $BRANCH -w . -p $PARAMETER -r $REGION
        '''.stripIndent().trim())
    }
}
