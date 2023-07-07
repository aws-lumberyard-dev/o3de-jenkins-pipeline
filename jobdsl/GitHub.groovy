folder('Github')

pipelineJob('projects_periodic_trigger') {
    definition {
        cps {
            sandbox(true)
            script('''
                pipeline {
                    agent { label 'controller' }
                    stages {
                        stage('Inviting ${GITHUB_USER} to ') {
                            steps {
                                timeout(time: 10, unit: 'MINUTES') {
                                        params.REPOS -> repo
                                        script {
                                            def resp = sh(script: """curl \
                                                                    -X PUT \
                                                                    -H "Accept: application/vnd.github+json" \
                                                                    -H "Authorization: Bearer <YOUR-TOKEN>"\
                                                                    -H "X-GitHub-Api-Version: 2022-11-28" \
                                                                    https://api.github.com/repos/OWNER/REPO/collaborators/USERNAME \""", returnStdout: true).trim()
                                            def respObj = readJSON text: resp
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            ''')
        }
    }
    logRotator {
        daysToKeep(7)
        numToKeep(14)
    }
    parameters {
        stringParam('GITHUB_USER', '', 'The Github user to add. Be sure to verify that the user is from Amazon')
        textParam {
            name('REPOS')
            defaultValue('''
                o3de
                o3de-atomsampleviewer
                o3de-multiplayersample
                o3de.org
            '''.stripIndent().trim())
            description('The repos to add the user to')
            trim(true)
        }
    }
}
