# Android CI/CD workflow configuration

The `Build, Test, and Publish Android Bundle` workflow uploads the generated Android App Bundle (AAB) and related build reports to AWS S3. The workflow is defined in [`.github/workflows/android-build-upload.yml`](../../.github/workflows/android-build-upload.yml).

## Required GitHub secrets

The workflow fails immediately if any of the following repository secrets are missing. Populate them before running the workflow:

| Secret | Purpose | How to obtain it | How to add it |
| --- | --- | --- | --- |
| `AWS_ACCESS_KEY_ID` | Programmatic access key ID for the CI IAM user. | 1. In the AWS Console, open **IAM**.<br>2. Create (or select) a user dedicated to CI deployments.<br>3. Attach a policy that grants `s3:PutObject`, `s3:PutObjectAcl`, and `s3:ListBucket` on the target bucket.<br>4. Generate an access key for that user and copy the **Access key ID**. | In your GitHub repository, go to **Settings → Secrets and variables → Actions → New repository secret**, name it `AWS_ACCESS_KEY_ID`, and paste the value. |
| `AWS_SECRET_ACCESS_KEY` | Secret key paired with the IAM access key. | While creating the IAM access key above, copy the **Secret access key**. AWS only shows it once—store it securely. | Add it as the `AWS_SECRET_ACCESS_KEY` secret in **Settings → Secrets and variables → Actions**. |
| `AWS_REGION` | AWS region that hosts the destination bucket. | From the AWS Console, note the region displayed in the bucket's **Properties** (e.g., `us-east-1`). | Add it as the `AWS_REGION` secret in **Settings → Secrets and variables → Actions**. |
| `AWS_S3_BUCKET` | Name of the S3 bucket that will store build artifacts. | Create or choose an S3 bucket in the AWS Console. Ensure the CI IAM user has write permissions. | Add it as the `AWS_S3_BUCKET` secret in **Settings → Secrets and variables → Actions**. |
| `PENNYBLOOM_SIGNING_STORE_BASE64` | Base64-encoded Android release keystore. | If you already have a `pennybloom.jks` file, run `base64 -w0 keystore/pennybloom.jks` locally and copy the output. To generate a new keystore: `keytool -genkeypair -v -keystore pennybloom.jks -storetype JKS -keyalg RSA -keysize 2048 -validity 10000 -alias pennybloom`. | Create a secret named `PENNYBLOOM_SIGNING_STORE_BASE64` and paste the Base64 string. |
| `PENNYBLOOM_SIGNING_STORE_PASSWORD` | Password used to open the keystore. | Record the password specified when the keystore was generated. | Add it as a secret named `PENNYBLOOM_SIGNING_STORE_PASSWORD`. |
| `PENNYBLOOM_SIGNING_KEY_ALIAS` | Alias of the signing key stored in the keystore. | Use the alias supplied to `keytool` (defaults to `pennybloom` in the command above). | Add it as a secret named `PENNYBLOOM_SIGNING_KEY_ALIAS`. |
| `PENNYBLOOM_SIGNING_KEY_PASSWORD` | Password for the private key identified by the alias. | Record the key password you configured when generating the keystore. | Add it as a secret named `PENNYBLOOM_SIGNING_KEY_PASSWORD`. |

> ℹ️ **Tip:** When pasting Base64 keystore content into GitHub, ensure there are no accidental newline characters. If you need to reissue credentials, delete the existing secret and add a fresh value.

## What the workflow does

1. Validates that all required secrets exist and instructs contributors to follow this guide if any are missing.
2. Sets up the Java 17 toolchain and required Android SDK components.
3. Reconstructs the signing keystore from the stored Base64 secret.
4. Runs unit tests via `./gradlew testDebugUnitTest`.
5. Builds the release Android App Bundle with `./gradlew bundleRelease`.
6. Collects the generated bundle, ProGuard mapping (if available), and unit-test results.
7. Uploads the collected artifacts to both GitHub Actions and the configured AWS S3 bucket under `android/<branch>/<run-id>/`.

If any step fails, the workflow stops immediately so that broken builds never reach S3.
