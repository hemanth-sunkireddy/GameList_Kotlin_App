# GameList Android App Hemanth
An Android application built with MVVM Architecture that fetches free-to-play games data from the [MMOBomb API](https://www.mmobomb.com/api1/games). The app features internal JSON file caching for offline support, reactive UI state handling, and bookmarking capabilities.

### Pre-requisites
- Android Studio: [Installation Instructions](https://developer.android.com/studio/install)
- Install Emulator: [Setup Instructions](https://developer.android.com/studio/run/emulator)

### Run Instructions
Open the project in the Android Studio. Choose the Device and click run button.

### Test Instructions
From the root folder of the project, run the following command:
```bash
./gradlew test
```

### Key Libraries and Tools
- Jetpack Compose & Material 3
- Retrofit 2
- Gson
- Glide Compose Integration
- MockK
- kotlinx-coroutines-test
- JUnit5
- roboelectric
