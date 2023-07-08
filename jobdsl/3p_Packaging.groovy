folder('3p_Packaging') {
    displayName('3P Packaging')
}

freeStyleJob('3p_Packaging/3p_System_Promote_Dev_to_Prod') {
    displayName('Promote 3P from Dev to Prod')
    label('linux-ubuntu-22-arm')
    logRotator {
        daysToKeep(720)
    }
    parameters {
        stringParam('Package_Name', '', 'Prefix name of the package (ex python-3.7.5-rev3-windows)')
        choiceParam('Dev_Bucket', ['s3://ly-dev-3p-packages'], '')
        choiceParam('Prod_Bucket', ['s3://ly-prod-3p-packages'], '')
    }
    properties {
        authorizationMatrix {
            inheritanceStrategy {
                inheriting()
                permissions([
                    'GROUP:hudson.model.Item.Build:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Item.Cancel:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Item.Configure:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Item.Read:authenticated',
                    'GROUP:hudson.model.Item.Read:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Item.Workspace:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Run.Replay:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Run.Update:${/jenkins/config/admin}'
                ])
            }
        }
    }
    steps {
        shell('''
            #!/bin/bash
            aws s3 cp ${Dev_Bucket}/ ${Prod_Bucket}/ --recursive --exclude "*" --include "${Package_Name}*" --acl bucket-owner-full-control
            aws s3 cp ${Dev_Bucket}/ "${Prod_Bucket/ly/o3de}"/ --recursive --exclude "*" --include "${Package_Name}*" --acl bucket-owner-full-control  
        '''.stripIndent().trim())
        descriptionSetterBuilder {
            description('${Package_Name}')
            regexp('')
        }
    }
}

freeStyleJob('3p_Packaging/Validate_Package') {
    label('linux')
    logRotator {
        daysToKeep(365)
    }
    parameters {
        stringParam('Package_Name', '', 'Prefix name of the package (ex python-3.7.5-rev3-windows)')
        choiceParam('Dev_Bucket', ['s3://ly-dev-3p-packages'], '')
    }
    properties {
        authorizationMatrix {
            inheritanceStrategy {
                inheriting()
                permissions([
                    'GROUP:hudson.model.Item.Build:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Item.Cancel:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Item.Configure:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Item.Read:authenticated',
                    'GROUP:hudson.model.Item.Read:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Item.Workspace:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Run.Replay:${/jenkins/config/admin}',
                    'GROUP:hudson.model.Run.Update:${/jenkins/config/admin}'
                ])
            }
        }
    }
    steps {
        shell('''
            #!/bin/bash
            sudo apt-get update 1> /dev/null
            sudo apt-get install -y clamav tree 1> /dev/null
            sudo freshclam 1> /dev/null
            git clone --quiet https://git-codecommit.us-west-2.amazonaws.com/v1/repos/Validation /tmp/Validation 1> /dev/null
        '''.stripIndent().trim())
        shell('''
            #!/bin/bash
            aws s3 cp ${Dev_Bucket}/ /tmp/ --recursive --exclude "*" --include "*${Package_Name}*" 1> /dev/null
        '''.stripIndent().trim())
        shell('''
            #!/bin/bash

            package="${Package_Name}"
            platforms=(Linux Mac Windows)
            validator_script="Validation/scripts/scrubbing/validator.py"

            cd /tmp

            for pack in *"$package"*.tar.xz ; do
            bn="$(basename "$pack" .tar.xz)"
            mkdir -p "$bn"
            tar xf "$pack" -C "$bn"
            echo ------------------------
            echo Validating "$bn"
            echo ------------------------

            printf "\\n--- Output of tree \\n"
            tree "$bn"

            printf "\\n--- Scanning package for viruses \\n"
            clamscan -r -i "$bn"/

            printf "\\n--- Checking package for restricted terms \\n"
            package_platform=${bn##*-}
            if [[ "$package_platform" =~ "multiplatform" ]]; then
                for p in ${platforms[@]} ; do
                    python3 $validator_script --package_platform "$p" -a "$bn"
                done
            elif [[ "$package_platform" =~ "ios" ]]; then
                python3 $validator_script --package_platform Mac -a "$bn"
            elif [[ "$package_platform" =~ "aarch64" ]]; then
                python3 $validator_script --package_platform Linux -a "$bn"
            elif [[ "$package_platform" =~ "Android" ]]; then
                python3 $validator_script --package_platform Windows -a "$bn"
            else
                python3 $validator_script --package_platform "${package_platform^}" -a "$bn"
            fi

            done
        '''.stripIndent().trim())
        descriptionSetterBuilder {
            description('${Package_Name}')
            regexp('')
        }
    }
}
