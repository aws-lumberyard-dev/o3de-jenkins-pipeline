multibranchPipelineJob('O3DE-LY-Fork_periodic-incremental-daily-internal') {
    branchSources {
        branchSource {
            source {
                github {
                    id('O3DE-LY-Fork_periodic-incremental-daily-internal')
                    configuredByUrl(false)
                    credentialsId('github')
                    repoOwner('aws-lumberyard-dev')
                    repository('o3de')
                    repositoryUrl('https://github.com/aws-lumberyard-dev/o3de.git')
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
        workflowBranchProjectFactory {
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
