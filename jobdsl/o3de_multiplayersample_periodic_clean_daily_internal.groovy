multibranchPipelineJob('o3de-multiplayersample_periodic-clean-daily-internal') {
    branchSources {
        branchSource {
            source {
                github {
                    id('o3de-multiplayersample_periodic-clean-daily-internal')
                    configuredByUrl(false)
                    credentialsId('github')
                    repoOwner('aws-lumberyard-dev')
                    repository('o3de-multiplayersample')
                    repositoryUrl('https://github.com/aws-lumberyard-dev/o3de-multiplayersample.git')
                    traits {
                        authorInChangelogTrait()
                        gitHubBranchDiscovery {
                            strategyId(3)
                        }
                        pruneStaleBranchTrait()
                        pruneStaleTagTrait()
                    }
                }
            }
            strategy {
                namedBranchesDifferent {
                    defaultProperties {
                        suppressAutomaticTriggering {
                            triggeredBranchesRegex('^$')
                        }
                    }
                }
            }
        }
    }
    description('Multiplayer Sample based on Spectra Technology')
    factory {
        remoteJenkinsFileWorkflowBranchProjectFactory {
            fallbackBranch('development')
            localMarker('')
            matchBranches(true)
            remoteJenkinsFileSCM {
                gitSCM {
                    userRemoteConfigs {
                        userRemoteConfig {
                            url('https://github.com/aws-lumberyard-dev/o3de.git')
                            name('')
                            refspec('')
                            credentialsId('github')
                        }
                        branches {
                            branchSpec {
                                name('development')
                            }
                        }
                        browser {}
                        gitTool(null)
                    }
                }
            }
            scriptPath('scripts/build/Jenkins/Jenkinsfile')
        }
    }
    orphanedItemStrategy {
        discardOldItems {
            daysToKeep(7)
            numToKeep(14)
        }
    }
}
