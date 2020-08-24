# gdx-controllerutils

Utilities for using controllers with libGDX, and enhanced drop-in replacements for the official gdx-controllers extension

![Compile and publish local](https://github.com/MrStahlfelge/gdx-controllerutils/workflows/Compile%20and%20publish%20local/badge.svg?branch=master&event=push)
![Maven Central](http://maven-badges.herokuapp.com/maven-central/de.golfgl.gdxcontrollerutils/gdx-controllerutils-mapping/badge.svg)

## Subprojects

All subprojects are technically independant, you can use only one of them in your project. They are bundled in a single Github project because all have something to do with Game Controller Input and libGDX.


### core-advcontrollers
Advanced core interfaces giving you more control over connected controllers: Rumble, query available buttons and axis, query standard button constants, and more.

[Documentation](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Advanced-Controller-interface)

### desktop-jamepad
[Jamepad](https://github.com/williamahartman/Jamepad) implementation for libGDX' controller interfaces. Bring hotplugging to Lwjgl2 by just changing your gradle file!

[Documentation](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Jamepad-controller-implementation)

### ios-controllers
iOS controller implementation (MFI and iCade) for libGDX' controller interfaces.

[Documentation](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/iOS-controller-implementation)

### gwt-controllers
The official GWT implementation, but with enhancements, regular releases and bugfixes down to libGDX 1.9.5+.

[More info](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/GWT-controller-implementation)

### android-controllers
The official Android implementation, but with enhancements, regular releases and bugfixes down to libGDX 1.9.11.
Sorry! Due to a breaking change in the android backend, our Android controllers implementation here is only compatible with 1.9.11 and up.
You can use version 1.0.1 if you are on an older libGDX Version.

### core-scene2d
Key and button supporting Stage with focusable Actors for Scene2d. This is also usable in games without Game Controller support.

[Documentation](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Button-operable-Scene2d)

### core-mapping
Support configurable mappings for game controllers in your projects.

[Documentation](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Configurable-Game-Controller-Mappings)

[More info](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Android-controller-implementation)

## Demos and examples

core-scene2d is shown with sources in [raeleus' Shadow Walker UI demo](https://github.com/raeleus/Shadow-Walker-UI).

All subprojects are used in my game [Falling Lightblocks Web/Android/iOS/FireTV](https://www.golfgl.de/lightblocks/).
Take a look at the [sources](https://github.com/MrStahlfelge/lightblocks).

## Installation

This project is published to the Sonatype Maven repository. You can integrate the lib into your project by just adding the dependencies to your `build.gradle` file.

Define the version of this API right after the gdxVersion:

    gdxVersion = '1.9.11' //or another gdx version you use
    cuversion = '2.0.0' // or 1.0.1 if you are on gdx <= 1.9.10

Then add the needed dependencies to your project. You will find the artifact ids on the subproject's wiki sites.

### Building from source
To build from source, clone or download this repository, then open it in Android Studio. Perform the following command to compile and upload the library in your local repository:

    gradlew clean uploadArchives -PLOCAL=true
    
See `build.gradle` file for current version to use in your dependencies.

## News & Community

You can get help on the [libgdx discord](https://discord.gg/6pgDK9F).

## License

The project is licensed under the Apache 2 License, meaning you can use it free of charge, without strings attached in commercial and non-commercial projects. We love to get (non-mandatory) credit in case you release a game or app using this project!
