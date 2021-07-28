import groovy.json.JsonSlurperClassic

pipeline {
    agent any

    options {
        timeout(time: 20, unit: 'MINUTES')
        ansiColor("xterm")
        timestamps ()
    }
	
    environment {
        PYTHONUNBUFFERED=1
    }

    stages {
        stage('Clean last build') {
            steps {
                cleanWs()
            }
        }

        stage ('Deploying multiple hosts') {   
            steps {
                script {
                    def deploys = [:]
                    def resp = sh (script: "#!/bin/sh -e\n curl -X GET 'https://github.com/moody1511/PythonLinux.git'",
                        returnStdout: true)
                    def json = new groovy.json.JsonSlurperClassic().parseText(resp)
                }
            } 
            steps {
                script {
                    servers.each { server ->
                        deploys[server] = {
                            sshagent(credentials: ['WorkerSSH']) {
                                if ( "${host}" == "kworker1" ) {
                                    appuser = "moody"
                                } else {
                                    appuser = "root"
                                }    
                              sh """   
                                [ -d ~/.ssh ] || mkdir ~/.ssh && chmod 0700 ~/.ssh
                                ssh -o StrictHostKeyChecking=no moody@$server touch /tmp/test1
                                """
                            }
                        }
                    }
                    parallel deploys
                }
            }
        }
    }
}