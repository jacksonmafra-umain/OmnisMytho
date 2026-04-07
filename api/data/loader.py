"""
Data loader for Omnis Mytho API.
Loads from generated/content.json if available, otherwise falls back to seed_data.py.
"""

import json
from pathlib import Path

from models.mythology import Alignment, Entity, EntityType, Mythology

_GENERATED = Path(__file__).parent / "generated" / "content.json"


def _load_generated() -> tuple[list[Mythology], list[Entity]]:
    """Load from the generated content.json file."""
    with open(_GENERATED, encoding="utf-8") as f:
        data = json.load(f)

    mythologies = []
    for m in data["mythologies"]:
        mythologies.append(Mythology(
            id=m["id"],
            name=m["name"],
            origin=m["origin"],
            description=m["description"],
            entity_count=m.get("entity_count", 0),
        ))

    entities = []
    for e in data["entities"]:
        entities.append(Entity(
            id=e["id"],
            name=e["name"],
            type=e.get("type", "god"),
            title=e.get("title", ""),
            description=e.get("description", ""),
            appearance=e.get("appearance", ""),
            powers=e.get("powers", []),
            symbols=e.get("symbols", []),
            personality=e.get("personality", ""),
            alignment=e.get("alignment", "neutral"),
            mythology_id=e.get("mythology_id", ""),
            image_prompt=e.get("image_prompt", ""),
        ))

    # Update entity counts
    for m in mythologies:
        m.entity_count = sum(1 for e in entities if e.mythology_id == m.id)

    return mythologies, entities


def _load_seed() -> tuple[list[Mythology], list[Entity]]:
    """Load from the static seed_data.py."""
    from data.seed_data import ENTITIES as seed_entities
    from data.seed_data import MYTHOLOGIES as seed_mythologies
    return seed_mythologies, seed_entities


def load_data() -> tuple[list[Mythology], list[Entity]]:
    """Load mythology data. Prefers generated content.json, falls back to seed_data."""
    if _GENERATED.exists():
        try:
            mythologies, entities = _load_generated()
            print(f"Loaded {len(mythologies)} mythologies, {len(entities)} entities from content.json")
            return mythologies, entities
        except Exception as e:
            print(f"Failed to load content.json: {e}, falling back to seed_data")

    mythologies, entities = _load_seed()
    print(f"Loaded {len(mythologies)} mythologies, {len(entities)} entities from seed_data")
    return mythologies, entities


MYTHOLOGIES, ENTITIES = load_data()
