freeStyleJob('pipeline_metrics_upload_nightly') {
    label('windows-b3c8994f1')
    parameters {
        stringParam('JENKINS_URL', '${/jenkins/config/url}', '')
        stringParam('PIPELINE_NAME', 'O3DE-LY-Fork_daily-pipeline-metrics', '')
        stringParam('BRANCH_NAME', 'Spectra_Mainline', '')
        credentialsParam('JENKINS_USERNAME') {
            defaultValue('${/jenkins/config/serviceuser/username}')
            type('com.cloudbees.plugins.credentials.common.StandardCredentials')
        }
        credentialsParam('JENKINS_API_TOKEN') {
            defaultValue('${/jenkins/config/serviceuser/token}')
            type('com.cloudbees.plugins.credentials.common.StandardCredentials')
        }
        stringParam('BUCKET', 'ec2bi-lumberyard', '')
        stringParam('CSV_REGEX', '(.*csv)', '')
        stringParam('CSV_PREFIX', 'spectra_build_metrics/data', '')
        stringParam('MANIFEST_REGEX', '(.*manifest)', '')
        stringParam('MANIFEST_PREFIX', 'spectra_build_metrics/manifest', '')
        stringParam('DAYS_TO_COLLECT', '1', '')
    }
    properties {
        pipelineTriggers {
            triggers {
                cron {
                    spec('H 22 * * * ')
                }
            }
        }
    }
    publishers {
        mailer('ly-infra@amazon.com', false, false)
    }
    scm {
        git {
            branch('jenkins_pipeline_metrics')
            remote {
                name('')
                refspec('')
                url('https://git-codecommit.us-west-2.amazonaws.com/v1/repos/Lumberyard')
            }
        }
    }
    steps {
        shell('''
            set +x

            # Grab the private IP of the load balancer to allow the EC2 instance to use the security group allowlist
            jenkinsip=$(aws ec2 describe-network-interfaces --filters Name=description,Values="ELB app/prod-jenkins-server/2af6c1cd4bb121e7" --query 'NetworkInterfaces[*].PrivateIpAddresses[*].PrivateIpAddress' --output text | head -n1 | tr -d '\\r')

            # Create a new hosts entry for the private IP. Note: The hosts file path only works for Windows Cygwin
            echo $jenkinsip jenkins-pipeline.agscollab.com | tee -a /cygdrive/c/Windows/system32/drivers/etc/hosts
        '''.stripIndent().trim())
        shell('''
            set +x

            export AWS_STS_REGIONAL_ENDPOINTS=regional
            export ASSUME_ROLE_ARN=arn:aws:iam::646296105064:role/s3_for_ec2bi-lumberyard

            awscred=$(aws --region us-west-2 sts assume-role --role-arn $ASSUME_ROLE_ARN --role-session-name jenkins --endpoint-url https://sts.us-west-2.amazonaws.com | grep -w 'AccessKeyId\\|SecretAccessKey\\|SessionToken' | awk '{print $2}' | sed 's/\\"//g;s/\\,//')

            [ -z "$awscred" ] && { echo "Can't assume role!"; exit 1; }

            AWS_ACCESS_KEY_ID=$(echo $awscred | awk '{print $1}')
            AWS_SECRET_ACCESS_KEY=$(echo $awscred | awk '{print $2}')
            AWS_SECURITY_TOKEN=$(echo $awscred | awk '{print $3}')
            AWS_DEFAULT_REGION='us-west-2'

            cat > assumerole.bat << EOF
            @echo off
            SET AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
            SET AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY
            SET AWS_SECURITY_TOKEN=$AWS_SECURITY_TOKEN
            SET AWS_DEFAULT_REGION=$AWS_DEFAULT_REGION
            EOF
        '''.stripIndent().trim())
        batchFile('''
                set LY_3RDPARTY_PATH=C:/ly/3rdParty
                %WORKSPACE%/python/get_python.bat
            '''.stripIndent().trim()))
        batchFile('''
                echo Assuming role...
                call assumerole.bat
                del assumerole.bat
                cd %WORKSPACE%/scripts/build/tools
                %WORKSPACE%/python/python.cmd jenkins_pipeline_metrics.py
            '''.stripIndent().trim()))
        
    }
}
