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

        stage('Run Transaction Integration Test') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean -Dtest=TransactionServiceIT test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh 'docker build -t ${APP_IMAGE_NAME}:latest .'
            }
        }
    }
}
