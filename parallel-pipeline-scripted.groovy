import groovy.json.JsonSlurperClassic

node('jenkins') {
    def deploys = [:]
    def resp = sh (script: "#!/bin/sh -e\n curl -X GET 'https://github.com/moody1511/PythonLinux.git'",
        returnStdout: true)
    def json = new groovy.json.JsonSlurperClassic().parseText(resp)

        stage('Clean last build') {
                cleanWs()
        }
        stage('Git pull') {
                git(
                    url: 'git@github.com:moody1511/PythonLinux.git',
                    credentialsId: "jenkins-git",
                    branch: 'master'
                )
        }


        stage ('Deploying multiple hosts') {    
                script {
                    json["hosts"].each { host ->
                        deploys[host] = {
                                sshagent (credentials: ['SSH']) {
                                    if ( "${host}" == "kworker1" ) {
                                        appuser = "moody"
                                    } else {
                                        appuser = "root"
                                    }
                                    sh """
                                       [ -d ~/.ssh ] || mkdir ~/.ssh && chmod 0700 ~/.ssh
                                       ssh $appuser@$host touch /tmp/test1
                                    """
                                } 
                        }
                    }
                    parallel deploys
                }
        }
 }
