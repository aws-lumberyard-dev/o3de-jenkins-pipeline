folder('LFS')

freeStyleJob('LFS/CreateLFS-aws-lumberyard') {
    label('ubuntu-command')
    parameters {
        stringParam('ORG', 'aws-lumberyard', '')
        stringParam('REPO', '', '')
    }
    properties {
        authorizationMatrix {
            entries{
                group{
                    name('${/jenkins/config/team}')
                    permissions([
                        'com.cloudbees.plugins.credentials.CredentialsProvider.Create',
                        'com.cloudbees.plugins.credentials.CredentialsProvider.Delete',
                        'com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains',
                        'com.cloudbees.plugins.credentials.CredentialsProvider.Update',
                        'com.cloudbees.plugins.credentials.CredentialsProvider.View',
                        'hudson.model.Item.Build',
                        'hudson.model.Item.Cancel',
                        'hudson.model.Item.Configure',
                        'hudson.model.Item.Delete',
                        'hudson.model.Item.Discover',
                        'hudson.model.Item.Move',
                        'hudson.model.Item.Read',
                        'hudson.model.Item.Workspace',
                        'hudson.model.Run.Delete',
                        'hudson.model.Run.Replay',
                        'hudson.model.Run.Update',
                        'hudson.scm.SCM.Tag'
                    ])
                }
            }
            inheritanceStrategy {
                inheriting()
            }
        }
    }
    scm {
        git {
            branch('*/s3-permissions-fix')
            remote {
                credentials('github')
                name('')
                refspec('')
                url('https://github.com/amzn-changml/github-aws-lfs-service.git')
            }
        }
    }
    steps {
        shell('''
            #!/bin/bash

            echo ***Setting up the environment

            sudo apt-get install -y python-is-python3
            sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common unzip jq 
            curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add 
            sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
            sudo apt-get update
            sudo apt-get install -y docker-ce
            sudo service docker start
            sudo usermod -a -G docker lybuilder

            curl -OL https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip
            unzip aws-sam-cli-linux-x86_64.zip -d sam-installation
            sudo ./sam-installation/install
            sam --version  && rm -rf ./sam-installation

            cd github_lfs_service/lfs_lambda
            sudo sam build --use-container

            cd ../

            sudo docker run --rm -v $(pwd):/tmp/layer lambci/yumda:2 bash -c "curl -sSf 'https://packagecloud.io/install/repositories/github/git-lfs/config_file.repo?os=amzn&dist=2&source=script' > /lambda/etc/yum.repos.d/github_git-lfs.repo && yum -q makecache -y --disablerepo='*' --enablerepo='github_git-lfs' --enablerepo='github_git-lfs-source' && yum install -y git git-lfs && mv /lambda/usr/bin/git-lfs /lambda/opt/bin/git-lfs && cd /lambda/opt && zip -yr /tmp/layer/git_layer.zip ."

            cp -r ${WORKSPACE}/github_lfs_service/lfs_lambda/.aws-sam/build/LFSFunction/* ${WORKSPACE}/github_lfs_service/lfs_lambda/       
        '''.stripIndent().trim())
        shell('''
            #!/bin/bash

            echo ***Setting up CDK environment

            python -m pip install -r ${WORKSPACE}/requirements.txt

            python -m pip install pytest

            curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
            export NVM_DIR="$HOME/.nvm"
            [ -s "$NVM_DIR/nvm.sh" ] && \\. "$NVM_DIR/nvm.sh"  # This loads nvm
            [ -s "$NVM_DIR/bash_completion" ] && \\. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion

            nvm install node

            npm install -g aws-cdk

            sudo ln -sf $(which node) /usr/local/bin/node
            sudo ln -sf $(which cdk) /usr/local/bin/cdk
        '''.stripIndent().trim())
        shell('''
            #!/bin/bash

            #echo ***Running tests

            #python -m pytest
        '''.stripIndent().trim())
        shell('''
            #!/bin/bash

            echo ***Getting Cloudfront certs

            echo "$(aws ssm get-parameter --region us-east-1 --name /github-lfs-service/all_private_key --with-decryption | jq -r '.Parameter.Value')" | openssl rsa -pubout -out all_public_key.pem
            echo "$(aws ssm get-parameter --region us-east-1 --name /github-lfs-service/private_key --with-decryption | jq -r '.Parameter.Value')" | openssl rsa -pubout -out get_public_key.pem

            #cdk synth --all
        '''.stripIndent().trim())
        shell('''
            echo ***Creating LFS infra for ${ORG}/${REPO}

            rm -rf ${WORKSPACE}/cdk.out # https://github.com/aws/aws-cdk/issues/13131#issuecomment-781921888

            sed -e "s/ExampleOwner/${ORG}/g" -e "s/ExampleRepository/${REPO}/g" ${WORKSPACE}/default-app-config.json > ${ORG}-${REPO}-app-config.json

            cdk deploy --require-approval never --all -c config=${ORG}-${REPO}-app-config.json 
        '''.stripIndent().trim())
    }
    wrappers {
        timestamps()
    }
}

freeStyleJob('LFS/CreateLFS-Internal') {
    label('ubuntu-command')
    parameters {
        stringParam('ORG', 'aws-lumberyard', '')
        stringParam('REPO', '', '')
    }
    properties {
        authorizationMatrix {
            entries{
                group{
                    name('${/jenkins/config/team}')
                    permissions([
                        'com.cloudbees.plugins.credentials.CredentialsProvider.Create',
                        'com.cloudbees.plugins.credentials.CredentialsProvider.Delete',
                        'com.cloudbees.plugins.credentials.CredentialsProvider.ManageDomains',
                        'com.cloudbees.plugins.credentials.CredentialsProvider.Update',
                        'com.cloudbees.plugins.credentials.CredentialsProvider.View',
                        'hudson.model.Item.Build',
                        'hudson.model.Item.Cancel',
                        'hudson.model.Item.Configure',
                        'hudson.model.Item.Delete',
                        'hudson.model.Item.Discover',
                        'hudson.model.Item.Move',
                        'hudson.model.Item.Read',
                        'hudson.model.Item.Workspace',
                        'hudson.model.Run.Delete',
                        'hudson.model.Run.Replay',
                        'hudson.model.Run.Update',
                        'hudson.scm.SCM.Tag'
                    ])
                }
            }
            inheritanceStrategy {
                inheriting()
            }
        }
    }
    scm {
        git {
            branch('*/main')
            remote {
                credentials('github')
                name('')
                refspec('')
                url('https://github.com/aws-lumberyard/github-aws-lfs-service.git')
            }
        }
    }
    steps {
        shell('''
            #!/bin/bash

            echo ***Setting up the environment

            sudo apt install -y apt-transport-https ca-certificates curl software-properties-common unzip jq python3 python3-pip python-is-python3
            curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add 
            sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
            sudo apt update
            sudo apt install -y docker-ce
            sudo service docker start
            sudo usermod -a -G docker lybuilder

            curl -OL https://github.com/aws/aws-sam-cli/releases/latest/download/aws-sam-cli-linux-x86_64.zip
            unzip aws-sam-cli-linux-x86_64.zip -d sam-installation
            sudo ./sam-installation/install
            sam --version  && rm -rf ./sam-installation

            cd github_lfs_service/lfs_lambda
            sudo sam build --use-container

            cd ../

            sudo docker run --rm -v $(pwd):/tmp/layer lambci/yumda:2 bash -c "curl -sSf 'https://packagecloud.io/install/repositories/github/git-lfs/config_file.repo?os=amzn&dist=2&source=script' > /lambda/etc/yum.repos.d/github_git-lfs.repo && yum -q makecache -y --disablerepo='*' --enablerepo='github_git-lfs' --enablerepo='github_git-lfs-source' && yum install -y git git-lfs && mv /lambda/usr/bin/git-lfs /lambda/opt/bin/git-lfs && cd /lambda/opt && zip -yr /tmp/layer/git_layer.zip ."

            cp -r ${WORKSPACE}/github_lfs_service/lfs_lambda/.aws-sam/build/LFSFunction ${WORKSPACE}/github_lfs_service/lfs_lambda/ 
        '''.stripIndent().trim())
        shell('''
            #!/bin/bash

            echo ***Setting up CDK environment

            python -m pip install -r ${WORKSPACE}/requirements.txt

            python -m pip install pytest

            curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.1/install.sh | bash
            export NVM_DIR="$HOME/.nvm"
            [ -s "$NVM_DIR/nvm.sh" ] && \\. "$NVM_DIR/nvm.sh"  # This loads nvm
            [ -s "$NVM_DIR/bash_completion" ] && \\. "$NVM_DIR/bash_completion"  # This loads nvm bash_completion

            nvm install node

            npm install -g aws-cdk

            sudo ln -sf $(which node) /usr/local/bin/node
            sudo ln -sf $(which cdk) /usr/local/bin/cdk
        '''.stripIndent().trim())
        shell('''
            #!/bin/bash

            echo ***Running tests

            python -m pytest
        '''.stripIndent().trim())
        shell('''
            #!/bin/bash

            get_param() {
                P=$(aws ssm get-parameter --region us-east-1 --name "$1" --with-decryption | jq -r '.Parameter.Value')
                echo $P
            }

            echo ***Getting Cloudfront certs

            echo "$(aws ssm get-parameter --region us-east-1 --name /github-lfs-service/all_private_key --with-decryption | jq -r '.Parameter.Value')" | openssl rsa -pubout -out all_public_key.pem
            echo "$(aws ssm get-parameter --region us-east-1 --name /github-lfs-service/private_key --with-decryption | jq -r '.Parameter.Value')" | openssl rsa -pubout -out get_public_key.pem

            cdk synth --all
        '''.stripIndent().trim())
        shell('''
            echo ***Creating LFS infra for ${ORG}/${REPO}

            rm -rf ${WORKSPACE}/cdk.out # https://github.com/aws/aws-cdk/issues/13131#issuecomment-781921888

            sed -e "s/ExampleOwner/${ORG}/g" -e "s/ExampleRepository/${REPO}/g" ${WORKSPACE}/default-app-config.json > ${ORG}-${REPO}-app-config.json

            cdk deploy --require-approval never --all -c config=${ORG}-${REPO}-app-config.json
        '''.stripIndent().trim())
    }
    wrappers {
        timestamps()
    }
}
