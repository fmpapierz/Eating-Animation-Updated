# Validation - Minecraft 26.2

Verified locally with JDK 25 and Gradle 9.5.1.

| Check | Result |
| --- | --- |
| `gradlew clean build` | PASS: all four release JARs |
| Common Minecraft codec and built-in pack test | PASS |
| Fabric 0.19.5 loader smoke client | PASS |
| Quilt 0.30.1 native loader smoke client | PASS |
| Forge 65.1.3 loader smoke client | PASS |
| NeoForge 26.2.0.86 loader smoke client | PASS |
| Asset reference validation | PASS: 1,253 models; all bundled animation textures resolve |
| Packaged JAR audit | PASS: loader metadata, 43 vanilla definitions, 271 support definitions, LICENSE, no test classes |

The common test decodes all 314 client-item definitions using Minecraft 26.2's actual codecs and validates resource-pack metadata and enumeration. Loader smoke tests additionally discover the mod with the actual loader, apply its production mixin, and verify the support pack is offered by ClientPackSource. NeoForge's test runs after its client mod initialization so its model-registration event handlers exist.

The support pack relies on 271 unanimated base textures from the matching third-party mods, just as upstream did. Those mods were not installed in these tests.

## Reproduce

```powershell
.\gradlew.bat :common:test
.\gradlew.bat :fabric:runClient :quilt:runClient :forge:runClient :neoforge:runClient -PsmokeTest
.\gradlew.bat clean build
python tools/validate_assets.py C:\path\to\minecraft-merged.jar
```

The smoke profile exits after validation, before opening a game window, and cannot build release JARs. The final clean build removes all smoke classes and restores the production mixin configuration.

## Remaining manual coverage

Interactive gameplay and visual frame timing have not been tested. Before publishing, test eating an apple, bread and dried kelp; drinking a colored potion, milk and honey; cancelling use halfway through; both hands and third-person views; remote players; inventory icons; resource reloads; toggling the support pack; and any desired third-party mod combinations. Also test the packaged JARs in normal launcher instances. Loader smoke checks use development classpaths.

Benign local warnings include Windows performance-counter queries, Minecraft command ambiguities, and Quilt's absent intermediary mappings for the unobfuscated game. Quilt's Fabric Loom manifest reports the Fabric loader/mixin version as unknown because this project uses Quilt's installer libraries explicitly; native Quilt metadata and startup checks pass.
