resource "kubernetes_secret_v1" "app_db_credentials" {
  metadata {
    name      = var.app_db_secret_name
    namespace = var.kube_namespace
  }

  type = "Opaque"

  data = {
    DB_USERNAME = var.app_db_username
    DB_PASSWORD = random_password.app_db_password.result
    JWT_SECRET  = random_password.jwt_secret.result
  }
}
