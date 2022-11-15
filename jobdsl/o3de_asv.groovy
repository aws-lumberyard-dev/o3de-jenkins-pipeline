multibranchPipelineJob('O3DE-ASV') {
    branchSources {
        branchSource {
            source {
                github {
                    id('O3DE-ASV-GitHub')
                    repoOwner('aws-lumberyard-dev')
                    repository('o3de-atom-sampleviewer')
                    repositoryUrl('https://github.com/aws-lumberyard-dev/o3de-atom-sampleviewer')
                    configuredByUrl(false)
                    credentialsId('github-access-token')
                    traits {
                        gitHubBranchDiscovery {
                            strategyId(3)
                        }
                        gitHubPullRequestDiscovery {
                            strategyId(1)
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
                            name('main')
                        }
                        named {
                            name('development')
                        }
                    }
                }
            }
        }
    }
    description('Pipeline Job for the GE&DS fork of the O3DE repo.')
    displayName('O3DE ASV-Fork [Branches]')
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
    configure {
        def traits = it / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
        traits << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
            strategyId(1)
            trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
        }
    }
}
