variable "kubeconfig_path" {
  description = "Path to the kubeconfig file Terraform should use for Kubernetes API access."
  type        = string
  default     = "~/.kube/config"
}

variable "kube_context" {
  description = "Kubernetes context Terraform should target."
  type        = string
  default     = "minikube"
}

variable "kube_namespace" {
  description = "Namespace where Terraform-managed Kubernetes resources should be created."
  type        = string
  default     = "sample-bank"
}

variable "postgres_image" {
  description = "Docker image used for the external PostgreSQL runtime."
  type        = string
  default     = "postgres:15"
}

variable "postgres_container_name" {
  description = "Container name for the Terraform-managed PostgreSQL runtime."
  type        = string
  default     = "sample-bank-postgres"
}

variable "postgres_port" {
  description = "Host port exposed for the external PostgreSQL runtime."
  type        = number
  default     = 5432
}

variable "postgres_db" {
  description = "Primary application database name."
  type        = string
  default     = "sample_bank"
}

variable "postgres_admin_user" {
  description = "Administrative PostgreSQL user used to bootstrap the database container."
  type        = string
  default     = "postgres"
}

variable "app_db_username" {
  description = "Application database username exposed to sample-bank."
  type        = string
  default     = "sample_bank"
}

variable "app_db_secret_name" {
  description = "Name of the Kubernetes secret that will hold the application database credentials."
  type        = string
  default     = "sample-bank-db-secret"
}
