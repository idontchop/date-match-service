pipeline {
    
    agent {
        docker {
            image 'openjdk:11-jdk'
            reuseNode true
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