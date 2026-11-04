# Khahish Android starter

This is a native Android Jetpack Compose UI prototype.

## Included
- Premium green-and-white Material 3 design
- Sample chat list and conversation screen shell
- Updates, Calls and Settings tabs
- Application ID: `com.khahish.app`

## Important
This is a UI prototype only. Sample chats are local demo data. OTP authentication, real-time messaging, media upload, notifications, voice/video calling, contact sync, backups, moderation and end-to-end encryption are not implemented yet.

## Build
Open this folder in Android Studio, allow Gradle sync, then choose Build > Build Bundle(s) / APK(s) > Build APK(s). Debug APK output: `app/build/outputs/apk/debug/`.

## Backend next phase
Create a Firebase project, register Android app ID `com.khahish.app`, add the Firebase Android configuration file, then integrate Authentication, Firestore, Storage and Cloud Messaging with secure rules.
