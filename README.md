# R & M Cards

## About the Project

A simple project based on the [Rick and Morty API](https://rickandmortyapi.com/)

<div align="center">
  <a href="https://github.com/BrianJr03/Rick-And-Morty/blob/develop/screenshots/rm_video.mp4">
    <img src="./screenshots/rick.png" width="300" alt="Demo Video"/>
  </a>
  <img src="./screenshots/rm_home_screen.jpg" width="300"  />
  <img style="margin-left: 300px" src="./screenshots/rm_dialog_screen.jpg" width="300"/>
</div>

## Features

- Search
  - Search for any Rick and Morty character by name
  - Fetch new characters in background
  - Auto-saves characters to local storage

- Animations
  - Enjoy smooth scrolling animations

## Prerequisites

- [Android Studio](https://developer.android.com/studio)
- Android SDK
  
## Installation

Feel free to download the latest release from one of the sources above.  
If you want to build it yourself, follow the steps below.

1. Clone the repo

   ```sh
   git clone https://github.com/BrianJr03/Rick-And-Morty.git
   ```

2. Open in Android Studio
3. Run on emulator or device

## Tech Stack

- Kotlin
- RoomDB
- DaggerHilt
- WorkManager
- Jetpack Compose
- MVVM with Repository
- Coroutines with Flows