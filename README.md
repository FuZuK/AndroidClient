# Android Notifications Application

### Configuration
##### Application configurations `app/src/main/res/raw/config.properties`
* `server_host` - the server host

##### Build configurations `app/build.properties`
* `applicationId` - the application name (required)
* `signingConfigs.release.storeFile` - path to the private key (not required)
* `signingConfigs.release.storePassword` - the store password (optional, if `storeFile` was specified)
* `signingConfigs.release.keyAlias` - the alias name (optional, if `storeFile` was specified)
* `signingConfigs.release.keyPassword` - the key password (optional, if `storeFile` was specified)

### Building
##### Build `debug` version
```bash
bash ./gradlew assembleDebug
```
##### Build `release` version
```bash
bash ./gradlew assembleRelease
```