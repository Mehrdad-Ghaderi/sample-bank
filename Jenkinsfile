pipeline {
    agent any

    environment {
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://host.docker.internal:5432/sample_bank'
        SPRING_DATASOURCE_USERNAME = 'sample_bank'
        SPRING_DATASOURCE_PASSWORD = 'sample_bank'
    }

    stages {
        stage('Verify Workspace') {
            steps {
                dir('/workspace') {
                    sh 'pwd'
                    sh 'ls -la'
                }
            }
        }

        stage('Run Transaction Integration Test') {
            steps {
                dir('/workspace') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean -Dtest=TransactionServiceIT test'
                }
            }
        }
    }
}
