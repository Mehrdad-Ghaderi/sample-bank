resource "kubernetes_namespace_v1" "sample_bank" {
  metadata {
    name = var.kube_namespace
  }
}
