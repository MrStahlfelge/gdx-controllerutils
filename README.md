# gdx-controllerutils

Utilitys for using controllers with libGDX

## Subprojects

All subprojects are technically independant, you can use only one of them in your project. They are bundled in a single Github project because all have something to do with Game Controller Input and libGDX.


### core-mapping
Support configurable mappings for game controllers in your projects.

[Documentation](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Configurable-Game-Controller-Mappings)

### core-scene2d
Key and button supporting Stage with focusable Actors for Scene2d. This is also usable in games without Game Controller support.

[Documentation](https://github.com/MrStahlfelge/gdx-controllerutils/wiki/Button-operable-Scene2d)

### desktop-jamepad
[Jamepad](https://github.com/williamahartman/Jamepad) implementation agains libGDX' controller interface. Brings hotplugging on Lwjgl2!

(Future work)

## Installation

Just add the needed dependencies to your project. You will find further information on the subproject's wiki sites.

Current snapshot version is:
`cuversion = '0.1.0-SNAPSHOT'`

### Building from source
To build from source, clone or download this repository, then open it in Android Studio. Perform the following command to compile and upload the library in your local repository:

    gradlew clean uploadArchives -PLOCAL=true
    
See `build.gradle` file for current version to use in your dependencies.

## Updates & News
Follow me to receive release updates about this

https://twitter.com/MrStahlfelge

# License

The project is licensed under the Apache 2 License, meaning you can use it free of charge, without strings attached in commercial and non-commercial projects. We love to get (non-mandatory) credit in case you release a game or app using this project!
