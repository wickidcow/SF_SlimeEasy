# Native floor and profile-preservation validation

## Scope

This batch continues from `072be6eda4ddcfd9d4df1c210979fe735fdb1fa3`. The previously packaged SlimeEasy 1.0.5 JAR contained 389 Java 25 classes despite the coordinated bundle's Java 21 / Minecraft 1.21.11 floor.

Keep the Java 25 build toolchain but explicitly emit Java 21 from both Kotlin and Java. Compile the normal distributable against the 1.21.11 Paper dev bundle and declare that API floor. Move the fake CraftPlayer wrapper to a thin Java subclass so newly introduced server overloads are inherited instead of linked through generated Kotlin overrides to newer-only types.

The historical machine-player UUID/name, virtual operator semantics and no-op setOp remain unchanged. No item IDs, research IDs, disk/storage identities, owner data, recipes, machine speeds, power costs or persisted formats are changed. No banner production code is changed; the regression now exercises the existing complete synchronization path with proper block support and checks exact patterns. Cargo wrapper preservation tests remain intact.

## Actual paired validation

[Slimefun-hosted run 36793150642](https://github.com/wickidcow/Slimefun-Legacy/actions/runs/36793150642) built these exact eight source/build files against the validated Legacy profile correction and ran one unchanged addon JAR on all three server lines:

| Platform | Actual runtime | Native/Cargo and database result |
| --- | --- | --- |
| Paper 1.21.11 | build132 / Java21 | Passed |
| Paper 26.2 | build129 / Java25 | Passed |
| Paper 26.3 | beta140 / Java25 | Passed |

The addon JAR SHA-256 was `a1c47c8bfe065973357d8f3175d91403121fe5eb757e7d68718ee36480900a90` on every runtime. All 389 base classes had major65, without newer or preview-required classes. The full Gradle build succeeded; it has no ordinary unit-test sources, so this is not described as a passing addon JUnit suite. Existing Kotlin deprecation warnings and an unchecked Java note remain.

The actual runtime checks covered stable fake-player identity/name and cache behavior, absence from online-player/operator lists, byte-identical ops.json, native positioning, real button activation and block destruction, configured butcher damage of exactly four health points, removal of a false kill marker from surviving targets, exact banner patterns through the complete production operation, and retained Cargo wrapper regressions.

The optional banner helper returned true on 1.21.11 and false on 26.2/26.3. The complete production path, including the existing fallback, passed on every version. Do not claim that the native-only helper passed everywhere or remove the fallback.

After shutdown the real SQLite database contained the exact machine UUID `5ab9c5fd-28e7-368d-b034-231adfa1d37d`, name `SE_Butcher`, backpack count0 and all 325 expected enabled research IDs. The expected IDs were captured from that runtime's registry and compared as a set and count. Foreign-key checks were empty; a separate read-only reopen confirmed the profile and research count. Server logs had no SQLITE_CONSTRAINT or foreign-key errors. These are generated fixture-server checks, not captures of an old owner world or a second full server startup.

## Required core correction

The original core could discard the supplied machine-player name by looking it up again using only the UUID. SQLite then ignored the null-name parent, and research-child writes failed. The paired run used the corrected Legacy candidate, JAR SHA-256 `488ea7e0f48453694f8a4e29f9ca148fac1b85973d52b5d273a06fd5f17e7d9e`, validated in [36793063530](https://github.com/wickidcow/Slimefun-Legacy/actions/runs/36793063530) and promoted to [Legacy PR292](https://github.com/wickidcow/Slimefun-Legacy/pull/292) at `c1e04521`.

The old smoke script's internal core filename includes 4.1.61; that was only a staging name. Exact candidate bytes and provenance were checked, and the actual core was the corrected 4.1.62 candidate. Ordinary addon CI using a different released core is not proof of this paired storage fix.

## Evidence and promotion boundaries

Build/source artifact `11133396164` SHA-256 `e9a485fef58c8421b7fe4e951b597c7494327a92a3c7016a3462ea68889a22d6` was downloaded; all eight Git source blobs were independently verified. Runtime artifacts were separately inspected and digest-checked: 1.21.11 `11133301625`, 26.2 `11133011223`, 26.3 `11132856284`. Their SQL reports, logs and input hashes agree.

The initial paired attempt stopped before addon compilation because its required core validation was still incomplete; the rerun retained that gate and used the successful exact core. Temporary source-staging/validation workflows are excluded from this promotion.

The addon version remains 1.0.5 as a development candidate, not a republished stable release. Normal PR checks, paired release-core validation, full addon-bundle packaging and old-item/world upgrade coverage remain required. The final bundle must distribute the baseline-built JAR; a newer-API compilation probe and its class-header check alone do not prove a universal native JAR. No master/main merge, stable release, production migration or item replacement is performed by this checkpoint.
