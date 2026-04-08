pipeline {
    agent any

    environment {
        DOCKER_HOST = 'unix:///var/run/docker.sock'
        TESTCONTAINERS_RYUK_DISABLED = 'true'
        TESTCONTAINERS_HOST_OVERRIDE = 'host.docker.internal'
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://host.docker.internal:5432/sample_bank'
        SPRING_DATASOURCE_USERNAME = 'sample_bank'
        SPRING_DATASOURCE_PASSWORD = 'sample_bank'
        POSTGRES_DB = 'sample_bank'
        POSTGRES_USER = 'sample_bank'
        POSTGRES_PASSWORD = 'sample_bank'
        DB_URL = 'jdbc:postgresql://postgres:5432/sample_bank'
        DB_USERNAME = 'sample_bank'
        DB_PASSWORD = 'sample_bank'
        APP_IMAGE_NAME = 'sample-bank-app'
        REGISTRY_HOST = 'ghcr.io'
        REGISTRY_OWNER = 'mehrdad-ghaderi'
        REGISTRY_IMAGE_NAME = 'sample-bank'
        GHCR_CREDENTIALS_ID = 'ghcr-io'
        KUBE_NAMESPACE = 'sample-bank'
        HELM_RELEASE_NAME = 'sample-bank'
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
                    def gitBranch = env.GIT_BRANCH?.trim()

                    if (!rawBranch && gitBranch) {
                        rawBranch = gitBranch
                    }
                    if (!rawBranch) {
                        rawBranch = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    }

                    def normalizedBranch = rawBranch
                        .replaceFirst(/^refs\/heads\//, '')
                        .replaceFirst(/^origin\//, '')
                        .replaceFirst(/^\*\//, '')
                    def safeBranchName = normalizedBranch.replaceAll(/[\\\/]/, '-')
                    def commitId = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    def buildNumber = env.BUILD_NUMBER?.trim()

                    echo "Raw BRANCH_NAME: ${env.BRANCH_NAME}"
                    echo "Raw GIT_BRANCH: ${env.GIT_BRANCH}"

                    if (!safeBranchName) {
                        error('GIT_BRANCH_NAME is blank; cannot build Docker image tag')
                    }
                    if (safeBranchName == 'HEAD') {
                        error('GIT_BRANCH_NAME resolved to HEAD; Jenkins branch metadata is not usable yet')
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
                    env.REGISTRY_REPOSITORY = "${env.REGISTRY_HOST}/${env.REGISTRY_OWNER}/${env.REGISTRY_IMAGE_NAME}"
                    env.REMOTE_TRACEABLE_TAG = "${env.REGISTRY_REPOSITORY}:${env.APP_IMAGE_TAG}"
                    env.REMOTE_LATEST_TAG = "${env.REGISTRY_REPOSITORY}:latest"

                    echo "Resolved branch name: ${env.GIT_BRANCH_NAME}"
                    echo "Resolved commit id: ${env.GIT_COMMIT_ID}"
                    echo "Resolved build number: ${buildNumber}"
                    echo "Using image tag: ${env.APP_IMAGE_TAG}"
                    echo "Using registry repository: ${env.REGISTRY_REPOSITORY}"
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

        stage('Prepare Registry Tags') {
            steps {
                script {
                    if (!env.REMOTE_TRACEABLE_TAG?.trim()) {
                        error('REMOTE_TRACEABLE_TAG is blank before registry tag preparation')
                    }
                    if (!env.REMOTE_LATEST_TAG?.trim()) {
                        error('REMOTE_LATEST_TAG is blank before registry tag preparation')
                    }

                    echo "Tagging ${env.APP_IMAGE_NAME}:${env.APP_IMAGE_TAG} as ${env.REMOTE_TRACEABLE_TAG}"
                    sh "docker tag ${env.APP_IMAGE_NAME}:${env.APP_IMAGE_TAG} ${env.REMOTE_TRACEABLE_TAG}"

                    if (env.GIT_BRANCH_NAME in ['develop', 'main']) {
                        echo "Tagging ${env.APP_IMAGE_NAME}:latest as ${env.REMOTE_LATEST_TAG}"
                        sh "docker tag ${env.APP_IMAGE_NAME}:latest ${env.REMOTE_LATEST_TAG}"
                    } else {
                        echo "Skipping remote latest tag because branch ${env.GIT_BRANCH_NAME} is not develop or main"
                    }
                }
            }
        }

        stage('Push Docker Image To GHCR') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: env.GHCR_CREDENTIALS_ID,
                    usernameVariable: 'GHCR_USERNAME',
                    passwordVariable: 'GHCR_TOKEN'
                )]) {
                    sh 'printenv GHCR_TOKEN | docker login "$REGISTRY_HOST" -u "$GHCR_USERNAME" --password-stdin'
                    sh "docker push ${env.REMOTE_TRACEABLE_TAG}"

                    script {
                        if (env.GIT_BRANCH_NAME in ['develop', 'main']) {
                            sh "docker push ${env.REMOTE_LATEST_TAG}"
                        } else {
                            echo "Skipping remote latest push because branch ${env.GIT_BRANCH_NAME} is not develop or main"
                        }
                    }
                }
            }
            post {
                always {
                    sh 'docker logout "$REGISTRY_HOST" || true'
                }
            }
        }

        stage('Verify Tagged Images Locally') {
            steps {
                script {
                    sh "docker image inspect ${env.REMOTE_TRACEABLE_TAG} > /dev/null"

                    if (env.GIT_BRANCH_NAME in ['develop', 'main']) {
                        sh "docker image inspect ${env.REMOTE_LATEST_TAG} > /dev/null"
                        echo "Prepared and pushed ${env.REMOTE_TRACEABLE_TAG} and ${env.REMOTE_LATEST_TAG}"
                    } else {
                        echo "Prepared and pushed ${env.REMOTE_TRACEABLE_TAG}"
                    }
                }
            }
        }

        stage('Deploy Immutable Image With Helm') {
            when {
                expression {
                    env.GIT_BRANCH_NAME == 'develop'
                }
            }
            steps {
                script {
                    if (!env.REMOTE_TRACEABLE_TAG?.trim()) {
                        error('REMOTE_TRACEABLE_TAG is blank before deployment')
                    }
                    if (!env.APP_IMAGE_TAG?.trim()) {
                        error('APP_IMAGE_TAG is blank before Helm deployment')
                    }
                }

                sh 'kubectl config current-context'
                sh """
                helm upgrade --install "$HELM_RELEASE_NAME" ./helm \
                  --namespace "$KUBE_NAMESPACE" \
                  --create-namespace \
                  --set image.tag="$APP_IMAGE_TAG"
                """
            }
        }

        stage('Verify Helm Deployment') {
            when {
                expression {
                    env.GIT_BRANCH_NAME == 'develop'
                }
            }
            steps {
                script {
                    sh 'kubectl rollout status deployment/sample-bank -n "$KUBE_NAMESPACE"'

                    def deployedImage = sh(
                        script: "kubectl get deployment sample-bank -n \"$KUBE_NAMESPACE\" -o jsonpath='{.spec.template.spec.containers[0].image}'",
                        returnStdout: true
                    ).trim()
                    def availableReplicas = sh(
                        script: "kubectl get deployment sample-bank -n \"$KUBE_NAMESPACE\" -o jsonpath='{.status.availableReplicas}'",
                        returnStdout: true
                    ).trim()

                    if (deployedImage != env.REMOTE_TRACEABLE_TAG) {
                        error("Helm deployment image ${deployedImage} does not match expected ${env.REMOTE_TRACEABLE_TAG}")
                    }
                    if (availableReplicas != '2') {
                        error("Expected 2 available replicas but found ${availableReplicas}")
                    }

                    echo "Deployed ${deployedImage} and verified Helm/Kubernetes rollout"
                }
            }
        }
    }
}
