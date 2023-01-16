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
                    echo "-------------Start of stage DeployServer-------------"
                    def server = "ubuntu@18.218.179.254 "
                    def path = "/home/ubuntu/app/dirForFiles"
                    def sshKey = "/home/ubuntu/app/dirForFiles/Ohio.pem"
                    def projectJar = "EsbParseCsvTransferToApi-1.0-SNAPSHOT.jar"
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
    def deploy = sh (script: "scp -r -i home/ubuntu/app/dirForFiles/Ohio.pem **/${projectJar} ${server}:${path}")
}

