library('jenkins-pipeline-library@master')

k8sClusterPipeline {
  k8sServiceName = "project-driver-account-api"
  k8sDeploymentName = "project-driver-account-api"
  k8sAccountID = "739171219021"
  k8sDevClusterName = "cluster.k8s.company-devel.com.br"
  k8sProdClusterName = "logistics.k8s.aws-production.dc-company.com"
  k8sAwsRegion = "sa-east-1"
  k8sToAwsRegion = "us-east-1"
  k8sJavaVersion = 11
  k8sIsJava = true
  buildUser = "jenkins"
  customDockerFile = "DockerfileAPI"
  k8sPath = "api/k8s"
}
