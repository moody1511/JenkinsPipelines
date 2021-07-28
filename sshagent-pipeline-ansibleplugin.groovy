
gitCredential = 'jenkins-git'

pipeline {
    agent {
        docker {
            image 'ubuntu:20.04'
            args '-u root:root'
            label 'jenkins4'
        }
    }
   // environment { 
            //SSH= sh (returnStdout: true, script: 'echo ssh').trim()
    //}
    stages {
        stage('Install python3 and ansible') {
            steps {
                sh "apt-get update && apt-get install -y openssh-client python3 python3-pip iputils-ping "
                sh "python3 -m pip install ansible "
            }
        }

        stage('Clone repository') {
            steps {
                git branch: "master", credentialsId: gitCredential, url: 'ggregreg'
            }
        }
        stage('Add SSH') {
            steps {
                ansiblePlaybook colorized: true, credentialsId: 'SSH', extras: '-i $HOST, -u $USER', installation: 'ansibletool', playbook: 'main.yml'             
            }
        }  
    }
}
