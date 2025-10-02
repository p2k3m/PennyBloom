# PennyBloom Onboarding

PennyBloom is a guardian-first finance companion for families. This repository contains the onboarding "welcome door" that helps parents verify themselves, link compliance documents, and create a safe child profile using a Compose-first Android experience.

## Modules
- **app** – Jetpack Compose Android app with guardian authentication, consent capture, and child profile scaffolding.

## Tech Stack
- Kotlin 1.9.25, Jetpack Compose 1.6.2, Material 3 teal theme
- Firebase Auth (v33.1.2) for OTP mock flow
- Hilt 2.52 for dependency injection
- DataStore and SharedPreferences for consent + profile persistence
- Firebase Analytics logging stub, Retrofit placeholder for future APIs

## Getting Started
1. Open the project in Android Studio Koala (2025.1) or newer.
2. Sync Gradle. A placeholder `google-services.json` is provided; replace with environment-specific config before shipping.
3. Run the app on a Pixel 8 (API 35) emulator to walk through:
   - Splash animation
   - Guardian OTP verification with mock Aadhaar upload
   - Child profile creation with age validation and consent

## Testing & Quality Gates
- `./gradlew test` – runs JUnit 5 unit tests (MockK, Turbine) for the `AuthViewModel`.
- `./gradlew connectedAndroidTest` – executes Espresso UI flows (requires emulator).
- `./gradlew jacocoTestReport` – generates JaCoCo coverage (goal ≥80% for auth module).
- `./gradlew bundleRelease` – assembles AAB for pipeline upload (`phase1/phase1-aab.aab`).

## Privacy & Compliance
- A privacy policy stub is located at `app/src/main/assets/privacy_policy_stub.html`. Update with legal copy prior to Play Store submission.
- Guardian consent is persisted with DataStore to align with DPDP Act guidelines.

## Analytics & Telemetry
- `FirebaseAnalyticsLogger` centralizes screen/action logging so that future metrics can be enabled without UI rewrites.

## Next Steps
- Integrate Digilocker SDK for real Aadhaar validation.
- Connect Retrofit placeholder to PennyBloom backend when APIs are ready.
- Automate Play Console internal track uploads once CI/CD pipeline is provisioned.
