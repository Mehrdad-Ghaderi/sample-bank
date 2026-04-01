pipeline {
    agent any

    environment {
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://host.docker.internal:5432/sample_bank'
        SPRING_DATASOURCE_USERNAME = 'sample_bank'
        SPRING_DATASOURCE_PASSWORD = 'sample_bank'
        APP_IMAGE_NAME = 'sample-bank-app'
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

                    def safeBranchName = rawBranch.replaceAll(/[\\\/]/, '-')
                    def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    def buildNumber = env.BUILD_NUMBER?.trim()

                    if (!safeBranchName) {
                        error('GIT_BRANCH_NAME is blank; cannot build Docker image tag')
                    }
                    if (!commitId) {
                        error('GIT_COMMIT_ID is blank; cannot build Docker image tag')
                    }
                    if (!buildNumber) {
                        error('BUILD_NUMBER is blank; cannot build Docker image tag')
                    }

                    env.GIT_BRANCH_NAME = safeBranchName
                    env.GIT_COMMIT_ID = commitId
                    env.APP_IMAGE_TAG = "${safeBranchName}-${buildNumber}-${commitId}"

                    echo "Resolved branch name: ${env.GIT_BRANCH_NAME}"
                    echo "Resolved commit id: ${env.GIT_COMMIT_ID}"
                    echo "Resolved build number: ${buildNumber}"
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
                script {
                    if (!env.APP_IMAGE_TAG?.trim()) {
                        error('APP_IMAGE_TAG is blank before docker build')
                    }

                    echo "Building Docker image ${env.APP_IMAGE_NAME}:${env.APP_IMAGE_TAG}"
                    sh "docker build -t ${env.APP_IMAGE_NAME}:${env.APP_IMAGE_TAG} -t ${env.APP_IMAGE_NAME}:latest ."
                }
            }
        }

        stage('Verify Docker Image Tags') {
            steps {
                sh "docker image inspect ${env.APP_IMAGE_NAME}:${env.APP_IMAGE_TAG} > /dev/null"
                sh "docker image inspect ${env.APP_IMAGE_NAME}:latest > /dev/null"
                echo "Built ${env.APP_IMAGE_NAME}:${env.APP_IMAGE_TAG} and ${env.APP_IMAGE_NAME}:latest"
            }
        }
    }
}
