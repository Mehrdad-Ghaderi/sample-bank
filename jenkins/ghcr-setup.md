# GHCR Setup For Jenkins

This pipeline pushes Docker images to GitHub Container Registry (`ghcr.io`).

## Registry naming

- Registry host: `ghcr.io`
- Repository: `ghcr.io/mehrdad-ghaderi/sample-bank`
- Immutable tag format: `<branch>-<jenkins-build-number>-<short-commit-sha>`
- Mutable tag: `latest`

Example immutable tag:

`ghcr.io/mehrdad-ghaderi/sample-bank:develop-12-a1b2c3d`

## Jenkins credential

Create one Jenkins credential with these values:

- Kind: `Username with password`
- ID: `ghcr-io`
- Username: your GitHub username
- Password: a GitHub Personal Access Token that can write packages

The pipeline uses the credential to run `docker login ghcr.io` and then pushes the built image tags.

## GitHub token requirements

Use a classic Personal Access Token or a fine-grained token with package write permission.

At minimum, Jenkins needs permission equivalent to:

- `write:packages`
- `read:packages`

If the first push is denied, also confirm the package visibility and repository linkage settings on GitHub.

## Branch behavior

- Every branch pushes an immutable traceable tag.
- Only `develop` and `main` push `latest`.

This prevents feature branches from moving a shared mutable tag.
