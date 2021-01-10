[![Downloads][downloads-shield]][downloads-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
<!-- [![MIT License][license-shield]][license-url] -->



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/Zazsona/MinecraftWearStatus">
    <img src="https://i.imgur.com/AEWJibu.png" alt="Logo" width="739" height="192">
  </a>

  <h3 align="center">Wear Status</h3>

  <p align="center">
    A Minecraft companion app for viewing status on WearOS
  </p>
</p>

<!-- ABOUT THE PROJECT -->
## About The Project
<p align="center">
  <a href="https://github.com/Zazsona/MinecraftWearStatus">
    <img src="https://i.imgur.com/IuWlIDj.png" alt="Logo" width="850" height="208">
  </a>
</p>

Wear Status is a WearOS app and Forge mod that ties the bridge between you and your Minecraft character, see your live game status right on your wrist, including health, hunger, time, and dimension. Watches with vibration support will also vibrate in reaction to damage - The more you take, the more it vibrates!

### Features:
* Realtime Minecraft status, right on your wrist!
* Automatic discovery and connection to Minecraft games.
* Configurable screen rotation to suit any gaming position.

## Installation
<p align="center">
  <a href="https://github.com/Zazsona/MinecraftWearStatus">
    <img src="https://i.imgur.com/R4wGNkj.png" alt="Logo" width="850" height="208">
  </a>
</p>

First, download the files (mod jar and watch APK) from [Releases](https://github.com/Zazsona/MinecraftWearStatus/releases)

### Minecraft
1. Download the Recommended version installer for [Minecraft Forge](http://files.minecraftforge.net/)
2. Run the installer, select "Install Client"
3. Place WearStatus.jar into the `mods` folder in your Minecraft installation

### WearOS
1. On your watch, enable developer mode
`Settings > System > About > Tap the build number multiple times`
2. Go into `Settings > Developer Settings` and enable ADB Debugging and Debugging over WiFi (Note the IP address)
2. If you do not have ADB on your PC, download the [Android ADK-Platform Tools](https://developer.android.com/studio/releases/platform-tools) for your system. 
4. Extract the folder.
5. Open cmd/terminal in the new folder (Shift+RightClick >  Open Powershell on Windows)
6. Connect ADB to the watch
   ```
   adb connect IP-ADDRESS-HERE
   ```
7. Install the app
   ```
   adb install PATH-TO-APK
   ```
8. Remember to turn off WiFi debugging!

<!-- CONTACT -->
## Contact

Zazsona - [@Zazsona](https://twitter.com/Zazsona)

Issues and Bugs: [https://github.com/Zazsona/MinecraftWearStatus/issues](https://github.com/Zazsona/MinecraftWearStatus/issues)


<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[downloads-shield]: https://img.shields.io/github/downloads/Zazsona/MinecraftWearStatus/total?style=flat-square
[downloads-url]: https://github.com/Zazsona/MinecraftWearStatus/releases
[stars-shield]: https://img.shields.io/github/stars/Zazsona/MinecraftWearStatus?style=flat-square
[stars-url]: https://github.com/Zazsona/MinecraftWearStatus/stargazers
[issues-shield]: https://img.shields.io/github/issues/Zazsona/MinecraftWearStatus?style=flat-square
[issues-url]: https://github.com/Zazsona/MinecraftWearStatus/issues
[license-shield]: https://img.shields.io/github/license/Zazsona/MinecraftWearStatus?style=flat-square
[license-url]: https://github.com/Zazsona/MinecraftWearStatus/blob/master/LICENSE.txt