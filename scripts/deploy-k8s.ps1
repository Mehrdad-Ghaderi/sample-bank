param(
    [string]$Namespace = "sample-bank",
    [string]$Image = "",
    [switch]$SkipRolloutWait
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$k8sDir = Join-Path $repoRoot "k8s"

$namespaceManifest = Join-Path $k8sDir "namespace.yaml"
$configMapManifest = Join-Path $k8sDir "configmap.yaml"
$secretManifest = Join-Path $k8sDir "secret.yaml"
$orderedManifests = @(
    $configMapManifest,
    (Join-Path $k8sDir "deployment.yaml"),
    (Join-Path $k8sDir "service.yaml")
)

Write-Host "Applying namespace manifest: $namespaceManifest"
kubectl apply -f $namespaceManifest

if (Test-Path $secretManifest) {
    Write-Host "Applying secret manifest: $secretManifest"
    kubectl apply -f $secretManifest
} else {
    Write-Host "Skipping secret apply because $secretManifest is missing"
}

foreach ($manifest in $orderedManifests) {
    Write-Host "Applying manifest: $manifest"
    kubectl apply -f $manifest
}

if ($Image) {
    Write-Host "Updating deployment image to: $Image"
    kubectl set image deployment/sample-bank app=$Image -n $Namespace
}

if (-not $SkipRolloutWait) {
    Write-Host "Waiting for deployment rollout to complete"
    kubectl rollout status deployment/sample-bank -n $Namespace
}
