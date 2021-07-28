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
                sh "apt-get update -y && apt-get install -y iputils-ping openssh-client"                
            }
        }
        stage('Build') {
            steps {
                script{
                    writeFile file: '/home/jenkins/workspace/ansiblepipeline@2/id_rsa', text:"${SSHTEXTTEST}"
                    sh "mkdir /root/.ssh"
                    sh "cp /home/jenkins/workspace/ansiblepipeline@2/id_rsa /root/.ssh/id_rsa"
                    sh "chmod 600 -R /root/.ssh"
                }
            }
        }
        stage('Run Ansible-playbook') {
            steps {
                sh 'ansible-playbook main.yml -i $HOST, -u $USER --private-key /root/.ssh/id_rsa'
            }
        }

    }
}