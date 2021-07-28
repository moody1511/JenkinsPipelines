pipeline {
    agent {
    dockerfile {
        args '-u root:root '
        filename 'Dockerfile'
        label 'jenkins4'
        }
    }
    stages {
        stage('Prepare SSH') {
            steps {
                sh "mkdir /root/.ssh"
                writeFile file: 'id_rsa', text: params.SSHPASS
                sh "cp $WORKSPACE/id_rsa /root/.ssh/id_rsa"
                sh "chmod 600 -R /root/.ssh/"                                
                } 
            }

       stage('Run ansible-playbook') {
            steps {
                sh "ansible-playbook main.yml -u ${params.USER} --private-key /root/.ssh/id_rsa -e 'ansible_host=${params.HOST}' -e 'ansible_remote_user=${params.USER}' -e 'ansible_timeout=100' -e 'ansible_python_interpreter=/usr/bin/python3' -i ${params.HOST},"
            }
       }
       stage('Delete image') {
            steps {
                sh "docker rmi ${Image.id}"
            }
       }


    }
}