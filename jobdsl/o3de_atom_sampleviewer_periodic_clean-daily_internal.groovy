multibranchPipelineJob('o3de-atom-sampleviewer_periodic-clean-daily-internal') {
    branchSources {
        branchSource {
            source {
                github {
                    id('o3de-atom-sampleviewer_periodic-clean-daily-internal')
                    configuredByUrl(false)
                    credentialsId('github')
                    repoOwner('aws-lumberyard-dev')
                    repository('o3de-atom-sampleviewer')
                    repositoryUrl('https://github.com/aws-lumberyard-dev/o3de-atom-sampleviewer.git')
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
                    namedExceptions {
                        named {
                            name('development')
                        }
                    }
                }
            }
        }
    }
    displayName('O3DE LY-Fork [Periodic Daily Incremental]')
    factory {
        remoteJenkinsFileWorkflowBranchProjectFactory {
            fallbackBranch('development')
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
