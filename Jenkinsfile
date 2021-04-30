pipeline {
    
    agent {
        docker {
            image 'maven:3.8.1-adoptopenjdk-11' 
            args '-v /home/jenkins/.m2:/home/jenkins/.m2' 
        }
    }
    
    environment {
        //Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
        IMAGE = readMavenPom().getArtifactId()
        VERSION = readMavenPom().getVersion()
    }
    
    stages {
        stage('Build') { 
            steps {
                sh 'mvn -B -DskipTests clean package' 
            }
        }
        stage('Build docker image') {
             steps {
                sh '''
                    /snap/bin/docker build -t ${IMAGE} .
                    /snap/bin/docker tag ${IMAGE} ${IMAGE}:${VERSION}
                    /snap/bin/docker push ${IMAGE}:${VERSION}
                '''
            }
        }
    }
}
