resource "null_resource" "wait_for_postgres" {
  depends_on = [docker_container.postgres]

  provisioner "local-exec" {
    command = <<EOT
for i in $(seq 1 30); do
  docker exec ${var.postgres_container_name} pg_isready -U ${var.postgres_admin_user} -d ${var.postgres_db} >/dev/null 2>&1 && exit 0
  sleep 2
done

echo "PostgreSQL did not become ready in time." >&2
exit 1
EOT
  }
}
