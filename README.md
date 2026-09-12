# Eating Animation - Minecraft 26.2

Maintained by **hooneybAdegrs**. [Website and source](https://github.com/fmpapierz/Eating-Animation-Updated) | [Report an issue](https://github.com/fmpapierz/Eating-Animation-Updated/issues)

Unofficial multiloader port of [Theoness1/EatingAnimation](https://github.com/Theoness1/EatingAnimation), based on upstream commit `a2cb5e0347383a1ffafc9a320b213268968bbfae` (MIT).

Adds progressive sprite animations while eating and drinking. Includes all 43 upstream vanilla item animations and the optional 271-item mod-support resource pack. Potion animation stages retain potion colors. Inventory icons, dropped items and item frames keep the normal model.

## Install

Use Java 25 and Minecraft **26.2**. Put exactly one matching JAR from `build/libs` in the client instance's `mods` folder:

| JAR | Loader |
| --- | --- |
| `eating-animation-fabric-1.10.0+mc26.2.jar` | Fabric 0.19.5+ |
| `eating-animation-forge-1.10.0+mc26.2.jar` | Forge 65.1.3+ (26.2) |
| `eating-animation-neoforge-1.10.0+mc26.2.jar` | NeoForge 26.2.0.86+ |
| `eating-animation-quilt-1.10.0+mc26.2.jar` | Quilt 0.30.1+ |

Fabric API and Quilt Standard Libraries are not required. This is a client visual mod; servers do not need it.

The built-in **Eating Animation - mod support** pack is available under Options > Resource Packs and is enabled by default when first discovered. It can be disabled or reordered. Its original mod IDs and textures are preserved; matching mods must exist for 26.2 and retain those item/texture paths. Compatibility with every third-party mod has not been tested.

## Build

Install JDK 25 and point `JAVA_HOME` to it. The checked-in wrapper uses Gradle 9.5.1.

```powershell
.\gradlew.bat build
```

On Linux/macOS: `./gradlew build`. Four installable JARs are collected in `build/libs`. Individual projects also produce source JARs.

## Layout

- `common/`: all shared Java, mixins, animation models, textures, built-in resource pack and tests.
- `fabric/`: Fabric metadata and development launcher.
- `forge/`: Forge entrypoint, metadata and build integration.
- `neoforge/`: NeoForge entrypoint, metadata and build integration.
- `quilt/`: native Quilt metadata, dependencies and development launcher.
- `smoke/`: opt-in checks executed inside each actual loader, excluded from release JARs.
- `tools/port_models.py`: one-time conversion used to migrate upstream legacy models.

The loader projects compile the same sources from `common/` into self-contained JARs. The common JAR is a development artifact and should not be installed.

Minecraft 26.2 ships unobfuscated code. Loom is used without remapping; ForgeGradle 7 and NeoForge ModDevGradle use the same game names. All item animations now use vanilla `using_item`, `use_duration` and `display_context` model properties. The only production mixin registers the optional resource pack. Sources compile with JDK 25 to Java 21 bytecode for Forge's Mixin compatibility; Minecraft itself still requires Java 25.

Quilt uses Fabric Loom's unobfuscated development support with Quilt's launcher and installer libraries explicitly configured. Its metadata uses Quilt's accepted intermediary identifier; there are no obfuscated game names to remap for 26.2.

## Checks

```powershell
.\gradlew.bat :common:test
.\gradlew.bat :fabric:runClient :quilt:runClient :forge:runClient :neoforge:runClient -PsmokeTest
```

The smoke clients validate all 314 item definitions with Minecraft's codecs, check resource-pack compatibility and verify the pack-registration mixin is applied, then exit before opening the game. Builds intentionally reject JAR packaging with `-PsmokeTest`. Run `build` without that property for release files.

For interactive testing use `:fabric:runClient`, `:forge:runClient`, `:neoforge:runClient` or `:quilt:runClient` without the smoke property. See [TESTING.md](TESTING.md) for coverage and remaining manual checks.

## Credits and license

Original authors: **theone_ss, spusik_, PinkGoosik, DoctorNight1**. Original artwork and MIT license are retained. This local port is not an official upstream release. See [LICENSE](LICENSE) and [NOTICE.md](NOTICE.md).
