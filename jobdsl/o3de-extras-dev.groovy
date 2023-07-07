multibranchPipelineJob('o3de-extras-dev') {
    branchSources {
        branchSource {
            source {
                github {
                    id('o3de-extras-dev')
                    configuredByUrl(false)
                    credentialsId('github')
                    repoOwner('aws-lumberyard-dev')
                    repository('o3de-extras')
                    repositoryUrl('https://github.com/aws-lumberyard-dev/o3de-extras.git')
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
    displayName('o3de-extras Dev Branches')
    factory {
        remoteJenkinsFileWorkflowBranchProjectFactory {
            fallbackBranch('development')
            remoteJenkinsFileSCM {
                gitSCM {
                    userRemoteConfigs {
                        userRemoteConfig {
                            url('https://github.com/aws-lumberyard-dev/o3de.git')
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
    triggers {
        periodicFolderTrigger {
            interval('2m')
        }
    }
}
