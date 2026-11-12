# Книги for Android

A native, offline version of the Russian-language reading demo in the parent directory. The Android project uses platform Views, the system keyboard and MediaPlayer. The APK includes the original covers, portraits and typefaces, the reading feed, and two Russian audio excerpts.

## Install on the Pixel 6a

Transfer the built [debug APK](app/build/outputs/apk/debug/app-debug.apk) to the phone and open it to install **Книги**. If Android asks, allow the app you used to open the APK to install it. For USB installation from this directory with USB debugging enabled, run the following command.

```sh
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Stakeholder walkthrough

1. Tap **Анна Каренина** on the welcome screen. The recap opens above the reading passage.
2. Tap **▶** to hear the passage. Try the 15-second rewind, 30-second skip, or drag the progress bar. Swipe up to read further and hide the reading controls, then tap the text to bring them back.
3. Tap the butterfly beside the question field. Drag its slider to change how many editorial inserts appear. Tap the butterfly again to return to the question field. Try **Кто такой Петрицкий?** to open the prepared character card.
4. Tap the down chevron in the header to browse discovery. Swipe a shelf sideways, then tap the floating **К чтению** card to return to your place.
5. Open **••• → К концу книги**. Rate the book, leave a review, and scroll past the film and theatre suggestions. Tap **Дама с собачкой** in **Почитать дальше** to read or listen to Chekhov. For a shorter route from the welcome screen, tap **короткие вечерние истории → Попробовать первые строки**.

To repeat the demo, open **••• → Начать демонстрацию заново** in the reader. This clears the saved reading position, reviews, questions and preferences.

## Build

Open this directory in Android Studio. Keep the parent repository's `assets/` and `fonts/` directories alongside `android/`. The build copies their artwork and fonts into the app.

With JDK 17 or newer and Android SDK Platform 36 installed, set `ANDROID_HOME` to the SDK directory or set `sdk.dir` in `local.properties`, then build the APK.

```sh
./gradlew :app:assembleDebug
```

The APK is compatible with Android 9 and newer. A first build needs internet access for Gradle and the Android build tools. The installed app works in airplane mode.

## Demo content

The feed follows the web prototype’s excerpt sequence, including its repeated Tolstoy passages. The bundled synthesized narration follows those passages for about 2 minutes 42 seconds and the Chekhov opening for about 35 seconds. Playback pauses when the app leaves the foreground. Questions and recommendations use prepared local responses. Reading position, appearance, ratings and reviews are stored on the device.
