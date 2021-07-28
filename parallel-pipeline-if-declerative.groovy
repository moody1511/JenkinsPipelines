pipeline {
    options {
        timeout(time: 10, unit: 'MINUTES')
        lock(resource: "marketmaker-tools", inversePrecedence: true)
        ansiColor("xterm")
        timestamps ()
    }

    environment {

    }
    
    agent { node { label "jenkins2 - worker" } }

    stages {
        stage('Clean last build') {
            steps {
                cleanWs()
            }
        }
        stage('Release') {
            steps {
                dir ("${path}") {
                    withEnv(['PATH+EXTRA=/usr/sbin:/usr/bin:/sbin:/bin']) {
                        script {
                            def deploys = [:]
                            "${servers}".tokenize(',').each { host ->
                                deploys[host]= {
                                    sshagent (credentials: ['jenkins-ansible']) {
                                        if ( "${host}" == "kworker1" ) {
                                            sshuser = "moody"
                                            } else {
                                            sshuser = "root"
                                        }                           
                                        if ( "${service}" != "none" ) {
                                            sh "ssh ${sshuser}@${host} 'touch /tmp/test1'"
                                        } else {
                                            sh "ssh ${sshuser}@${host} 'touch /tmp/test123 && echo "1234" > /tmp/test123"'"
                                        }
                                    }
                                }
                            }
                            parallel deploys
                        }
                    }
                }
            }
        }
    }
    
    post {
        always {
            withEnv(['PATH+EXTRA=/usr/sbin:/usr/bin:/sbin:/bin']) {
                script {
                    currentBuild.description = "${servers}: ${service}"
                }
            }
        }
    }
}
