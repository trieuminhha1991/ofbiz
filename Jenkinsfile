pipeline {
  agent {label'Master11'}
  // parameters {
  //       string(defaultValue: "test", description: 'What environment?', name: 'environment')
  //   }
  environment {
    DOCKER_REGISTRY = "docker-registry.olbius.com"
    TAG = sh(script: 'git rev-parse HEAD', returnStdout: true).trim()
    IMAGE = "${env.DOCKER_REGISTRY}/dev-core-dms:test-${env.TAG}"
  }
  stages {
    stage('Build') {
      steps {
        dir('ofbiz_src') {
          withDockerContainer("openjdk:7-jdk-alpine") {
            sh "./ant clean && ./ant build"
          }
        }
      }
    }
    stage('Docker') {
      steps {
        sh "docker build -t ${env.IMAGE} ."
        sh "docker push ${env.IMAGE}"
        sh "docker rmi ${env.IMAGE}"
      }
    }
    stage ('DEPLOY TEST') {
      steps {
        sh """
            kubectl -n hcm-test set image deployment/dms-olbius-com ofbiz=${env.IMAGE}
           """
      }
    }
  }
}