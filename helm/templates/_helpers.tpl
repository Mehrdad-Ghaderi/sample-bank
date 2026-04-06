{{- define "sample-bank.name" -}}
{{- default .Chart.Name .Values.app.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "sample-bank.fullname" -}}
{{- include "sample-bank.name" . -}}
{{- end -}}

{{- define "sample-bank.labels" -}}
app.kubernetes.io/name: {{ include "sample-bank.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}
