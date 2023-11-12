pipeline {
    agent any

    environment {
        dockerimagename = "ski"
        dockerImage = ""
        nexusRepositoryURL = "192.168.33.10:8081/repository/doghman/"
        nexusRepositoryName = "doghman"
        dockerImageVersion = "1.0"
    }

    stages {
        stage ('GIT') {
            steps {
                echo "Getting Project from Git"
                git branch: 'moetazDoghman',
                    url: 'https://github.com/Moetaz-doghman/SkiTest.git'
            }
        }

        stage("Build") {
            steps {
                sh "chmod +x ./mvnw"
                sh "mvn clean package -Pprod -X"
                sh "mvn --version"
            }
        }

        stage('Run JUnit and Mockito Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sh 'mvn sonar:sonar -Dsonar.login=admin -Dsonar.password=sonar'
            }
        }

        stage("Build Docker image") {
            steps {
                script {
                    try {
                        dockerImage = docker.build(dockerimagename, '.')
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        error("Docker build failed: ${e.message}")
                    }
                }
            }
        }

        stage("Deploy Artifact to Nexus") {
            steps {
                sh "mvn deploy -Pprod"
            }
        }

        stage("Deploy Docker Image to private registry") {
            steps {
                script {
                    def dockerImage = 'ski'
                    def dockerTag = 'latest'
                    def nexusRegistryUrl = '172.17.0.3:8082/repository/doghman/'
                    def dockerUsername = 'admin'
                    def dockerPassword = 'nexus'

                    sh "docker build -t ${dockerImage}:${dockerTag} ."
                    sh "docker tag ${dockerImage}:${dockerTag} ${nexusRegistryUrl}${dockerImage}:${dockerTag}"
                    sh "echo ${dockerPassword} | docker login -u ${dockerUsername} --password-stdin ${nexusRegistryUrl}"
                    sh "docker push ${nexusRegistryUrl}${dockerImage}:${dockerTag}"
                }
            }
        }

        stage("Start app and db") {
            steps {
                script {
                    def dockerImage = 'ski'
                    def dockerTag = 'latest'
                    def nexusRegistryUrl = '172.17.0.3:8082/repository/doghman/'
                    def dockerUsername = 'admin'
                    def dockerPassword = 'nexus'

                    sh "echo ${dockerPassword} | docker login -u ${dockerUsername} --password-stdin ${nexusRegistryUrl}"
                    sh "docker-compose pull"
                    sh "docker-compose up -d"
                }
            }
        }
    }


    post {
        always {


            emailext to: "moetaz.doghman@esprit.tn",
              subject: "jenkins build:${currentBuild.currentResult}: ${env.JOB_NAME}",
                             body: "${currentBuild.currentResult}: Job ${env.JOB_NAME}\nMore Info can be found here: ${env.BUILD_URL}",
                            attachLog: true

        }
    }
}