resource "postgresql_role" "app_user" {
  name     = var.app_db_username
  login    = true
  password = random_password.app_db_password.result

  depends_on = [null_resource.wait_for_postgres]
}
