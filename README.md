<p style="text-align: center; width: 100%; line-height: 0; font-size: 0;">
<img src="screenshots/top_banner.png" width="100%" style="vertical-align: bottom;">
</p>

## About
**_AudioFeels_** is a **Compose Multiplatform** audio player for [Audius](https://audius.co/), built for simple, **mood-based playlist matching**.

<details>
    <summary>Table of Contents</summary>
    <ol>
        <li><a href="#screenshots">Screenshots</a></li>
        <li><a href="#features">Features</a></li>
        <li><a href="#used-technologies">Used technologies</a></li>
    </ol>
</details>

## Screenshots
<p style="text-align: center; width: 100%; line-height: 0; font-size: 0;">
<img src="screenshots/discover.png" width="100%" style="vertical-align: bottom;"><img src="screenshots/responsive_player_ui.png" width="100%" style="vertical-align: bottom;"><img src="screenshots/dynamic_theme.png" width="100%" style="vertical-align: bottom;">
</p>

## Features
- **Playlist discovery** based on 
  - mood
  - trending status
  - text search
- **Carry on** playback
- **Responsive audio player**
- Track-based **dynamic theme**
- Live **audio visualization** on android

## Used technologies
- [Jetpack Navigation (aka "Navigation 2")](https://developer.android.com/jetpack/androidx/releases/navigation) - screen flows definition, backstack management
- [Room](https://developer.android.com/jetpack/androidx/releases/room) - database for on-device playlist/search suggestion storage
- [Datastore](https://developer.android.com/jetpack/androidx/releases/datastore) - user preference storage
- [Kotlin-inject](https://github.com/evant/kotlin-inject) - dependency injection
- [Ktor](https://ktor.io/) - network requests
- [Coroutines](https://kotlinlang.org/docs/coroutines-guide.html) - asynchronous/concurrent programming
- [Haze](https://github.com/chrisbanes/haze) - background blurring
