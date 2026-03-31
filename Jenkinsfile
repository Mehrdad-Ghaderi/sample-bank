pipeline {
    agent any

    environment {
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://host.docker.internal:5432/sample_bank'
        SPRING_DATASOURCE_USERNAME = 'sample_bank'
        SPRING_DATASOURCE_PASSWORD = 'sample_bank'
        APP_IMAGE_NAME = 'sample-bank-app'
        APP_IMAGE_TAG = ''
    }

    stages {
        stage('Checkout Source') {
            steps {
                checkout scm
            }
        }

        stage('Verify Workspace') {
            steps {
                sh 'pwd'
                sh 'ls -la'
            }
        }

        stage('Build Image Metadata') {
            steps {
                script {
                    def rawBranch = env.BRANCH_NAME?.trim()
                    if (!rawBranch) {
                        rawBranch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    }

                    env.GIT_BRANCH_NAME = rawBranch.replaceAll(/[\\\/]/, '-')
                    env.GIT_COMMIT_ID = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.APP_IMAGE_TAG = "${env.GIT_BRANCH_NAME}-${env.BUILD_NUMBER}-${env.GIT_COMMIT_ID}"

                    echo "Using image tag: ${env.APP_IMAGE_TAG}"
                }
            }
        }

        stage('Run Transaction Integration Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean -Dtest=TransactionServiceIT test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${APP_IMAGE_NAME}:${APP_IMAGE_TAG} -t ${APP_IMAGE_NAME}:latest .'
            }
        }

        stage('Verify Docker Image Tags') {
            steps {
                sh 'docker image inspect ${APP_IMAGE_NAME}:${APP_IMAGE_TAG} > /dev/null'
                sh 'docker image inspect ${APP_IMAGE_NAME}:latest > /dev/null'
                echo "Built ${env.APP_IMAGE_NAME}:${env.APP_IMAGE_TAG} and ${env.APP_IMAGE_NAME}:latest"
            }
        }
    }
}
