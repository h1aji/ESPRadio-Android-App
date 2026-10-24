# ESP Radio. Remote control app for Android

A native Android remote control app for the [ESP-radio](https://github.com/h1aji/Esp-radio) [ESP32-Radio](https://github.com/edzelf/ESP32-Radio) and firmware, built with Kotlin and Jetpack Compose.

The device exposes its controls over plain HTTP on the local network (no JSON API, just query-param GET requests). This app talks to it directly over Wi-Fi — no cloud, no account, no third-party server involved.

## Features

- Play/pause, volume up/down and direct volume control
- Browse and select presets, or play a custom station/stream URL
- Bass and treble (gain/frequency) tone controls
- Live device status polling
- Save and switch between multiple device IP addresses (up to 5 saved + 1 active)

## Requirements

- Android 8.0 (API 26) or higher
- A phone/tablet on the same local network as the ESP radio device
- The device's IP address (set it in the app's settings screen)

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- MVVM with `ViewModel` + `StateFlow`
- OkHttp for networking
- Jetpack DataStore (Preferences) for persisting saved device IPs

## Project structure

```
app/src/main/java/com/h1aji/espradio/
├── MainActivity.kt
├── data/
│   ├── EspRadioClient.kt      # HTTP client for the device's control API
│   ├── RadioState.kt          # UI state model
│   └── SettingsRepository.kt  # Persisted device IPs (DataStore)
├── viewmodel/
│   └── ControlViewModel.kt    # App state and device interaction logic
└── ui/
    ├── ControlScreen.kt
    ├── SettingsScreen.kt
    ├── components/            # Reusable Compose UI pieces
    └── theme/                 # Compose theming
```

## Building

Open the project in Android Studio and run it, or from the command line:

```bash
./gradlew assembleDebug
```

The debug APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## Usage

1. Install the app and open it.
2. Go to Settings and enter the IP address of your ESP radio device on the local network.
3. Return to the main screen — it will connect and show the device's current status.

## License

MIT
