# SlimeEasy maintenance contract

Maintain the existing SlimeEasy gameplay while supporting Minecraft 1.21.11 and newer. Preserve item/research IDs, persistent keys and value types, storage/network/disk identities, ownership, quantities, recipes, energy costs, production rates and Cargo transfer behavior. Do not rebuild old items from templates or discard unreadable data.

Use the checked-in Gradle wrapper. Build with the supported Java 25 toolchain while emitting Java 21 bytecode. Compile the distributable against the 1.21.11 native/API floor; use newer API overrides only for separate compilation probes. A Java 21 class header alone is not proof of native/API compatibility.

`./gradlew clean build cargoRegressionJar` builds the plugin and a separate test-only regression plugin. The production output is `build/libs/SF_SlimeEasy<version>.jar`. Do not package the regression plugin or a source archive as the installable plugin. Keep individual release assets as raw JARs; an aggregate addon ZIP is a separate core-release artifact.

Validate the same baseline-built JAR on Paper 1.21.11 with Java 21 and maintained newer Paper versions with their required Java runtimes. Keep the existing Cargo wrapper regressions and real native behavior tests. Preserve the machine player's UUID/name and virtual permission behavior; never write to the operator list to simulate its internal permissions.

Run banner checks through the complete production synchronization operation, including its existing Bukkit fallback, and separately report the optional native helper. Retain exact pattern assertions, valid block support and cleanup. Do not remove a fallback because one newer API compiles.

The paired profile/research runtime check requires the Slimefun Legacy profile-name correction documented in `docs/native-floor-validation.md`. Do not label ordinary CI using a different core as evidence for that exact pairing. Report compilation, runtime, persistence, warnings and remaining gaps separately. Existing warnings are not permission to add blanket suppressions.

Keep changes focused and English documentation clear. Do not overwrite concurrent branches or publish a release solely because compilation succeeded. Reconcile validated source revisions with the coordinated Slimefun Legacy addon bundle before release.
