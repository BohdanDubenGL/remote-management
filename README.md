# Remote Management

This mobile application for configuring and monitoring routers provides users with a convenient way to manage their network equipment without having to log into the web interface.

The application allows the user to view the current status of the router, see a list of connected devices, and receive information about their traffic.

In addition, the application provides the ability to manage Wi-Fi interfaces: change SSID, passwords, security mode, and access point state.

---

### Instructions on how to build a project

The `/.run` directory contains run files that the IDE detects and adds to run configurations

* `androidApp.run/xml` - build and run android app.
* `dependencyUpdates.run/xml` - checks project dependencies for updates.
* `desktopApp.run/xml` - build and run desktop app (JVM).
* `iosApp.run/xml` - build and run ios app.
* `jvmTests.run/xml` - build and run jvm tests.

You can also build project without IDE from the CLI:

* android: `./gradlew app:assemble`
* desktop: `./gradlew app:run`
* tests: `./gradlew domain:jvmTest app:desktopTest`
* update dependencies: `./gradlew dependencyUpdates`

---

This is a Kotlin Multiplatform project targeting Android, iOS, Web, Desktop.

* `/app` is for code that will be shared across the Compose Multiplatform application.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders (`androidMain`, `desktopMain`, `iosMain`) are for Kotlin code that will be compiled for only the platform indicated in the folder name.

* `/iosApp` contains iOS application.

* `/domain` contains domain entities, use cases, and other domain types.

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html),
[Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform/#compose-multiplatform),
[Kotlin/Wasm](https://kotl.in/wasm/)…
