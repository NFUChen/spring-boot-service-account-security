# Release Guide

This document explains how to create releases using GitHub Actions.

## Automated Release Process

### Method 1: Using Git Tags (Recommended)

1. Create and push a version tag:
```bash
git tag v1.0.0
git push origin v1.0.0
```

2. The GitHub Action will automatically:
   - Build the JAR file
   - Run tests
   - Create a GitHub release
   - Upload the JAR file as a release asset

### Method 2: Manual Release via GitHub UI

1. Go to your repository on GitHub
2. Click on **Actions** tab
3. Select **Build and Release JAR** workflow
4. Click **Run workflow**
5. Enter the version (e.g., `v1.0.0`)
6. Click **Run workflow**

## What Gets Created

Each release includes:
- ✅ **JAR file** (`spring-boot-service-account-starter-X.X.X.jar`)
- ✅ **Release notes** with usage instructions
- ✅ **File size** and build information
- ✅ **Download links** for easy access

## Version Naming Convention

Use semantic versioning: `vMAJOR.MINOR.PATCH`

Examples:
- `v1.0.0` - Initial release
- `v1.1.0` - New features
- `v1.0.1` - Bug fixes
- `v2.0.0` - Breaking changes

## Download Instructions for Users

After a release is created, users can download the JAR file by:

1. Going to the [Releases page](../../releases)
2. Clicking on the latest release
3. Downloading the JAR file from the **Assets** section

## Continuous Integration

The repository also includes a build workflow that runs on every push and pull request to ensure code quality and that the JAR can be built successfully.

## Troubleshooting

### Failed Release
If a release fails:
1. Check the **Actions** tab for error logs
2. Fix any build or test issues
3. Delete the failed tag: `git tag -d v1.0.0 && git push origin :refs/tags/v1.0.0`
4. Create the tag again with the fixes

### Re-releasing Same Version
GitHub doesn't allow duplicate releases. To re-release:
1. Delete the existing release from GitHub UI
2. Delete the tag: `git tag -d v1.0.0 && git push origin :refs/tags/v1.0.0`
3. Create the tag again: `git tag v1.0.0 && git push origin v1.0.0`
