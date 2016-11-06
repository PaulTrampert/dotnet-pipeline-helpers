# dotnet-pipeline-helpers

## nugetPipeline
### Usage
```groovy
nugetPipeline {
  gitRepoUrl = "https://sample.com/repo/url.git"
  gitCredentialsId = "gitCredentialsId"
  project = "Primary.Project"
  notificationRecipients = "who.to@notify.com"
}
```

### Parameters
#### `gitRepoUrl`
The url for the git repo. This is the same url you would pass to `git clone`.

#### `gitCredentialsId` (Optional)
If the repository is private, the id for the credentials to use for checkout.

#### `project`
The primary project name. This is the project that will be packaged.

#### `notificationRecipients` (Optional)
Static list of emails to notify failures to.

### Notes
* The pipeline assumes the following project structure:
```
.
+-- <project>
|  +-- project.json
+-- <project>.Test
|  +-- project.json
```
* The resulting nupkg will be placed in a directory at the root of the repo called `Artifacts`.
