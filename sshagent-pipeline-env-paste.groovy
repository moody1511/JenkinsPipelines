
gitCredential = 'jenkins-git'

pipeline {
    agent {
        docker {
            image 'ubuntu:20.04'
            args '-u root:root '
            label 'jenkins4'
        }
    }
    environment { 
            SSH= sh (returnStdout: true, script: 'echo ssh').trim()
    }

    stages {
        stage('Install python3 and ansible') {
            steps {
                sh "apt-get update && apt-get install -y openssh-client python3 python3-pip iputils-ping "
                sh "python3 -m pip install ansible "
            }
        }


        stage('Clone repository') {
            steps {
                git branch: "master", credentialsId: gitCredential, url: 'htrhtrh'
            }
        }

        stage('Write SSH') {
            steps{
                writeFile file: 'id_rsa', text: params.SSH
            }
        }     
        stage('Add SSH') {
            steps {
                sh 'mkdir ~/.ssh'
                sh 'ls -la $PWD'                
                sh 'cp $WORKSPACE/id_rsa /root/.ssh/id_rsa'
                sh 'chmod -R 600 /root/.ssh/'             
            }
        }
       stage('Run ansible-playbook') {
            steps {
                sh "ansible-playbook main.yml -u ${params.USER} --private-key /root/.ssh/id_rsa -e 'ansible_host=${params.HOST}' -e 'ansible_remote_user=${params.USER}' -e 'ansible_timeout=100' -e 'ansible_python_interpreter=/usr/bin/python3' -i ${params.HOST},"
            }
        }
    }
}
