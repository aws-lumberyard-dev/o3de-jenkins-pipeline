multibranchPipelineJob('o3de-atom-sampleviewer-dev') {
    branchSources {
        branchSource {
            source {
                github {
                    id('o3de-atom-sampleviewer-dev')
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
                }
            }
        }
    }
    configure {
        def traits = it / 'sources' / 'data' / 'jenkins.branch.BranchSource' / 'source' / 'traits'
        traits << 'org.jenkinsci.plugins.github__branch__source.ForkPullRequestDiscoveryTrait' {
            strategyId(1)
            trust(class: 'org.jenkinsci.plugins.github_branch_source.ForkPullRequestDiscoveryTrait$TrustPermission')
        }
    }
    displayName('Atom SampleViewer Dev Branches')
    factory {
        workflowBranchProjectFactory {
            scriptPath('Scripts/build/Jenkins/Jenkinsfile')
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
