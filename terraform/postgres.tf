resource "random_password" "postgres_admin_password" {
  length  = 24
  special = false
}

resource "random_password" "app_db_password" {
  length  = 24
  special = false
}

resource "docker_image" "postgres" {
  name = var.postgres_image
}

resource "docker_container" "postgres" {
  name  = var.postgres_container_name
  image = docker_image.postgres.image_id

  env = [
    "POSTGRES_DB=${var.postgres_db}",
    "POSTGRES_USER=${var.postgres_admin_user}",
    "POSTGRES_PASSWORD=${random_password.postgres_admin_password.result}"
  ]

  ports {
    internal = 5432
    external = var.postgres_port
  }

  restart = "unless-stopped"
}
