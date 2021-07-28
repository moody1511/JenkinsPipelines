pipeline {
    agent {
        docker {
            image 'moody1511/ubuntuansible:1.2.0'
            label 'jenkins4'
            args '-u root:root'
        }
    }

    stages {
        stage('Add ping ') {
            steps {
                sh "apt-get update -y && apt-get install -y iputils-ping openssh-client sshpass"                
            }
        }
        stage('Run Ansible-playbook') {
            steps {
                sh "mkdir /root/.ssh"
                sh "ssh-keygen -b 4096 -t rsa -f /root/.ssh/id_rsa -q -N ''"
                sh "chmod 600 -R /root/.ssh/"
                sh "sshpass -p '${SSHPASS}' ssh-copy-id -o 'StrictHostKeyChecking=no' -f -i /root/.ssh/id_rsa.pub ${USER}@${HOST}"                                  
                sh "ansible-playbook main.yml -i ${HOST}, -u ${USER}"     
            }
        }
    }
 }