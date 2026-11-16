# GLASS TV — Android TV launcher starter

A native Kotlin Android TV home-screen starter with:
- Permanent left-side Russian navigation
- Cinematic dark gradient background
- Frosted/translucent glass-style panels
- Installed TV/launcher app discovery
- D-pad focus and OK-to-launch behavior

## Build
Open this folder in Android Studio (Giraffe or newer), allow Gradle sync, then:
**Build > Build Bundle(s) / APK(s) > Build APK(s)**

APK output:
`app/build/outputs/apk/debug/app-debug.apk`

## Notes
- This is a starter build, not a signed Play Store release.
- The current glass effect is translucent layered styling; true real-time background blur can be added with RenderEffect on Android 12+.
- App categories are heuristic filters based on app names. "Настройки" is a placeholder for the next iteration.
- On some Android TV boxes, choosing a new Home app requires selecting GLASS TV in the system Home-app chooser.
- If your box runs Android 8+, it meets the declared minimum SDK (API 23).


## Build APK automatically with GitHub Actions

1. Upload the complete contents of this project to the `main` branch of your `Glas-tv` repository.
2. Open the repository's **Actions** tab.
3. Select **Build GLASS TV APK** and tap **Run workflow** (or wait for the push build).
4. When the run finishes successfully, open the run and download the **GLASS-TV-debug-APK** artifact.
5. Extract the downloaded artifact ZIP; it contains `app-debug.apk`.

This workflow builds a debug APK. It is suitable for testing and sideload installation; it is not a Play Store release-signed APK.
