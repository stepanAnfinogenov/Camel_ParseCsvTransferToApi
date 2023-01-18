pipeline {
    agent any

    tools {
        maven "maven-3"
    }

    stages {
        stage('Build') {
            steps {
                echo "-------------Start of stage Build-------------"
                sh 'mvn clean package'
                echo "-------------End of stage Build-------------"
            }
        }

        stage('DeployToServer') {
            steps {
                script{
                    echo "-------------Start of stage DeployServer------------"
                    def server = "ubuntu@3.144.238.92"
                    def path = "/home/ubuntu/app/dirForFiles"
                    def sshKey = "/Ohio.pem"
                    def projectJar = "**/*.jar"
                    deployJar(server, path, projectJar)
                    echo "-------------End of stage DeployServer-------------"
                }
            }
        }
    }

    post {
        always {
            echo "post section"
        }
    }
}

def deployJar(def server, def path, def projectJar) {
    def deploy = sh (script: "scp -r -i /Ohio.pem ${projectJar} ${server}:${path}")
}

