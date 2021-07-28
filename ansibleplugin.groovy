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
        stage('AnsiblePlaybook ') {
            steps {
                ansiblePlaybook colorized: true, credentialsId: 'SSH', extras: '-i $HOST, -u $USER', installation: 'ansibletool', playbook: 'main.yml'
            }
        }
    }
}