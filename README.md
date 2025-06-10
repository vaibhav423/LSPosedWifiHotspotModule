# WiFi Hotspot Module for LSPosed

![Build Status](https://github.com/USERNAME/LSPosedWifiHotspotModule/actions/workflows/build.yml/badge.svg)
[![GitHub Release](https://img.shields.io/github/v/release/USERNAME/LSPosedWifiHotspotModule)](https://github.com/USERNAME/LSPosedWifiHotspotModule/releases)

This LSPosed module allows you to modify WiFi hotspot settings (SSID and password) without requiring a device reboot.

## Problem Description
Normally, when you directly modify the WiFi hotspot configuration file at `/data/misc/apexdata/com.android.wifi/WifiConfigStoreSoftAp.xml`, changes don't take effect until the device is rebooted. This module applies those changes immediately.

## Features
- Change WiFi hotspot SSID and password without rebooting
- Simple user interface
- Automatic service restart to apply changes
- Change WiFi hotspot SSID and password without rebooting
- Simple user interface
- Automatic service restart to apply changes

## How It Works
The module uses Xposed framework hooks to:
1. Modify the WifiConfigStoreSoftAp.xml file directly
2. Restart relevant WiFi services programmatically
3. Force the system to reload hotspot configuration

## Requirements
- Android 8.0+ (API level 24+)
- Root access
- LSPosed framework installed 

## Installation
1. Build the module or download the APK
2. Install the APK on your device
3. Activate the module in LSPosed Manager
4. Reboot once after installation (only needed for the first time)
5. After that, you can change hotspot settings without rebooting

## Usage
1. Open the app
2. Enter your desired SSID and password
3. Tap "Apply Changes"
4. Changes will take effect immediately

## Technical Details
The module works by:
- Writing directly to `/data/misc/apexdata/com.android.wifi/WifiConfigStoreSoftAp.xml`
- Using reflection and system commands to restart relevant services
- Hooking into Android's WiFi service implementation

## Development
This module is developed using:
- Xposed API 82
- Android Gradle Plugin 7.4.2
- Java 11

## Permissions
The module requires the following permissions:
- Root access (for file operations and service management)

## Credits
Created as a solution for users who need to programmatically modify WiFi hotspot settings without rebooting their devices.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## CI/CD Pipeline
This project uses GitHub Actions for continuous integration and deployment:

### Automated Builds
- Every push to `main`, `master`, or `dev` branches triggers a new build
- Pull requests to `main` and `master` are automatically built and tested
- Build artifacts (debug and unsigned release APKs) are available as GitHub Actions artifacts

### Automated Releases
- Creating a new tag (e.g., `v1.0.0`) automatically triggers a release build
- Release APKs are automatically signed (if signing keys are configured) and uploaded to GitHub Releases

### Setting up Signing Keys
To enable automatic APK signing for releases:

1. Generate a keystore: 
   ```
   keytool -genkey -v -keystore release-key.keystore -alias release -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Base64 encode the keystore:
   ```
   base64 release-key.keystore > keystore-base64.txt
   ```
3. Add the following secrets in your GitHub repository settings:
   - `SIGNING_KEY`: Content of the keystore-base64.txt file
   - `ALIAS`: The alias used when creating the keystore (e.g., release)
   - `KEY_STORE_PASSWORD`: The keystore password
   - `KEY_PASSWORD`: The key password

## Contributing
1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## Versioning
- We use [SemVer](http://semver.org/) for versioning
- To create a new release, create and push a new tag:
  ```
  git tag -a v1.0.0 -m "Version 1.0.0"
  git push origin v1.0.0
  ```
