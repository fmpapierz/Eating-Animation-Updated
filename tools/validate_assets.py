"""Validate model/texture references against the port and a Minecraft 26.2 JAR.

Usage: python tools/validate_assets.py /path/to/minecraft-merged.jar
Third-party base textures are supplied by their respective mods.
"""
import json
import sys
from pathlib import Path
from zipfile import ZipFile

root = Path(__file__).resolve().parents[1] / 'common/src/main/resources'
with ZipFile(sys.argv[1]) as vanilla:
    names = set(vanilla.namelist())
    assert json.loads(vanilla.read('version.json'))['id'] == '26.2'
    errors = []
    external = set()
    count = 0
    for path in root.rglob('models/item/*.json'):
        count += 1
        data = json.loads(path.read_text(encoding='utf-8'))
        assert 'overrides' not in data, path
        compat = 'resourcepacks' in path.parts
        pack = root / 'resourcepacks/supporteatinganimation' if compat else root
        namespace = path.parent.parent.parent.name
        references = [('models', data['parent'], '.json')] if 'parent' in data else []
        references += [('textures', value, '.png') for value in data.get('textures', {}).values()]
        for kind, value, extension in references:
            if value.startswith('#'):
                continue
            ns, identifier = value.split(':', 1) if ':' in value else ('minecraft', value)
            relative = f'assets/{ns}/{kind}/{identifier}{extension}'
            if (pack / relative).exists() or (root / relative).exists() or relative in names:
                continue
            # Only unanimated, non-Minecraft base textures may come from external mods.
            if compat and ns == namespace and kind == 'textures' and not any(
                    marker in path.stem for marker in ('_eating_', '_drinking_')):
                external.add(relative)
            else:
                errors.append(f'{path.relative_to(root)} -> {relative}')
    if errors:
        raise SystemExit('\n'.join(errors))
    print(f'PASS: {count} models; all bundled animation textures resolve; '
          f'{len(external)} base textures are supplied by third-party mods.')
