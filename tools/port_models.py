"""One-time, reproducible conversion of upstream 1.21 item overrides to 26.2."""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1] / 'common/src/main/resources'

def write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(value, indent=2) + '\n', encoding='utf-8')

def convert():
    count = 0
    for path in sorted(ROOT.rglob('models/item/*.json')):
        data = json.loads(path.read_text(encoding='utf-8-sig'))
        overrides = data.pop('overrides', None)
        if not overrides:
            continue
        namespace = path.parent.parent.parent.name
        base_id = f'{namespace}:item/{path.stem}'
        def model(identifier):
            result = {'type': 'minecraft:model', 'model': identifier}
            if namespace == 'minecraft' and path.stem == 'potion':
                result['tints'] = [{'type': 'minecraft:potion', 'default': -13083194}]
            return result
        entries = []
        for override in overrides:
            predicate = override['predicate']
            assert set(predicate) <= {'eat', 'eating', 'drink', 'drinking'}, path
            threshold = predicate.get('eat', predicate.get('drink', 0))
            entries.append({'threshold': threshold, 'model': model(override['model'])})
        base = model(base_id)
        animation = {
            'type': 'minecraft:condition', 'property': 'minecraft:using_item',
            'on_false': base,
            'on_true': {
                'type': 'minecraft:range_dispatch', 'property': 'minecraft:use_duration',
                'remaining': False, 'scale': 1 / 30, 'fallback': base,
                'entries': sorted(entries, key=lambda entry: entry['threshold'])
            }
        }
        # Keep inventory, dropped items and frames unchanged, as upstream's GUI mixin did.
        definition = {'model': {
            'type': 'minecraft:select', 'property': 'minecraft:display_context',
            'cases': [{'when': ['firstperson_lefthand', 'firstperson_righthand',
                                 'thirdperson_lefthand', 'thirdperson_righthand'],
                       'model': animation}], 'fallback': base
        }}
        write(path, data)
        write(path.parent.parent.parent / 'items' / (path.stem + '.json'), definition)
        count += 1
    for pack in [ROOT, ROOT / 'resourcepacks/supporteatinganimation']:
        write(pack / 'pack.mcmeta', {'pack': {'min_format': 88, 'max_format': 88,
              'description': 'Eating Animation' if pack == ROOT else 'Eating Animation - mod support'}})
    print(f'Converted {count} animated item definitions.')

if __name__ == '__main__':
    convert()
