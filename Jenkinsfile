pipeline {
    agent any

    environment {
        DOCKERHUB_CREDENTIALS = credentials('stepananfi-dockerhubAccess')
        PROJECT_JAR = 'ParseCsvTransferToApi.jar'
        KEY_ACCESS_SERVER = '/keyAppServerIreland.pem'
        USER_HOST_SERVER = 'ubuntu@apps.anf2.com'
        PATH_APP_SERVER = '/home/ubuntu/app'
    }

    tools {
        maven "maven-3"
    }

    stages {
        stage('Build') {
            steps {
                echo "-------------Start of stage Build-------------"
                sh 'mvn clean package'
//                sh 'docker build -t stepananfi/spring_boot_crud_app:'+"BuildNumber-${env.BUILD_NUMBER}" + ' .'
                echo "-------------End of stage Build-------------"
            }
        }
/*
        stage('PushDockerHub') {
            steps {
                echo "-------------Start of stage Push-------------"
                sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
                sh 'docker push stepananfi/spring_boot_crud_app:'+"BuildNumber-${env.BUILD_NUMBER}"
                echo "-------------End of stage Push-------------"
            }
        }

        stage('Test') {
            steps {
                echo "-------------Start of stage Test-------------"
                sh 'mvn test'
                echo "-------------End of stage Test-------------"
            }
        }
*/

        stage('StopRunningAppOnServer') {
            steps {
                script{
                    echo "-------------Start of stage StopRunningAppOnServer-------------"
                    killRunningProject()
                    echo "-------------End of stage StopRunningAppOnServer-------------"
                }
            }
        }

        stage('DeployToServer') {
            steps {
                script{
                    echo "-------------Start of stage DeployServer-------------"
                    deployJar()
                    startJarOnServer()
                    echo "-------------End of stage DeployServer-------------"
                }
            }
        }
    }

    post {
        always {
        echo "post section"
//            sh 'docker logout'
        }
    }
}


def killRunningProject() {
    echo "killRunningProject started"
    def processesJava = sh (script: 'ssh -f -i ${KEY_ACCESS_SERVER} ${USER_HOST_SERVER} '+ "ps -ef | grep java", returnStdout:true)
    def result = "empty"
    processesJava.toString().split('\n').each {println it.split('\\s+')[1]}
    result = processesJava.toString().split('\n').findAll { it.endsWith(env.PROJECT_JAR) }
    echo result.toString()
    def numberJavaProcess = result.toString().split('\\s+')[1];
    sh 'ssh -f -i ${KEY_ACCESS_SERVER} ${USER_HOST_SERVER} '+ "kill -9 ${numberJavaProcess}"
    echo "killRunningProject ended"
}

def deployJar() {
    def deploy = sh (script: "scp -r -i ${KEY_ACCESS_SERVER} **/${PROJECT_JAR} ${USER_HOST_SERVER}:${PATH_APP_SERVER}")
}

def startJarOnServer() {
    sh 'ssh -f -i ${KEY_ACCESS_SERVER} ${USER_HOST_SERVER} '+ "bash ${PATH_APP_SERVER}/runProject-${PROJECT_JAR}.sh"
}
