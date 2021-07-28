pipeline {
    options {
        timeout(time: 10, unit: 'MINUTES')
        ansiColor("xterm")
    }

    agent {
        docker {
                args '-u root:root -e ANSIBLE_FORCE_COLOR=true -e PYTHONUNBUFFERED=1'
                image 'moody1511/ubuntuansible:1.2.0'
                label 'jenkins''
            }
        }

    stages {
        stage('Clone repository') {
            steps {
                git branch: 'master', credentialsId: 'git-cred', url: 'https://github.com/moody1511/PythonLinux.git'
            }
        }
        stage('Prepare sshagent and run Ansible-playbook') {
            steps {
                script {
                    sshagent (credentials: ['SSHPASS']) {
                        sh """
                            [ -d ~/.ssh ] || mkdir ~/.ssh && chmod 0700 ~/.ssh
                            ansible-playbook main.yml -u ${params.USER} --private-key ~/.ssh/id_rsa -e 'ansible_host=${params.HOST}' -e 'ansible_remote_user=${params.USER}' -e 'ansible_timeout=100' -e 'ansible_python_interpreter=/usr/bin/python3' -i ${params.HOST},
                        """
                    }
                }
            }
        }
    }
}
