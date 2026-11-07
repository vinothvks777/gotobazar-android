# GotoBazar Android app

A native Android WebView wrapper for the live GotoBazar store at https://gototop.in/.

- App label: GotoBazar
- Application ID: `in.gototop.gotobazar`
- Launcher icon: generated from the shop-bag mark in the supplied website logo
- Store pages, products, login, cart and checkout are served by the existing website.
- Only the GotoTop domain is kept inside the app; external links and payment apps are handed to Android.
- SSL certificate errors are rejected; certificate validation is not bypassed.

## Build

Open this folder in Android Studio and build `app` / `assembleDebug`, or push to GitHub. The included GitHub Actions workflow builds a debug APK and uploads it as `GotoBazar-Android-APK`.

The project intentionally does not include PHP/database credentials or website source files. It loads the live HTTPS store.
