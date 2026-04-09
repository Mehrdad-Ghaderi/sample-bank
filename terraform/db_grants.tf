resource "postgresql_grant" "app_database_connect" {
  database    = var.postgres_db
  role        = postgresql_role.app_user.name
  object_type = "database"
  privileges  = ["CONNECT", "CREATE", "TEMPORARY"]

  depends_on = [null_resource.wait_for_postgres]
}

resource "postgresql_grant" "app_public_schema_usage" {
  database    = var.postgres_db
  schema      = "public"
  role        = postgresql_role.app_user.name
  object_type = "schema"
  privileges  = ["USAGE", "CREATE"]

  depends_on = [null_resource.wait_for_postgres]
}
