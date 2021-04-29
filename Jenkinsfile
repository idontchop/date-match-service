pipeline {
    agent {
        docker {
            image 'maven:3.8.1-adoptopenjdk-11' 
            args '-v /root/.m2:/root/.m2' 
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
                    /usr/bin/docker build -t ${IMAGE} .
                    /usr/bin/docker tag ${IMAGE} ${IMAGE}:${VERSION}
                    /usr/bin/docker push ${IMAGE}:${VERSION}
                '''
            }
        }
    }
}