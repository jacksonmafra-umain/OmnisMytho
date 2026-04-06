"""
Image generation script for Omnis Mytho.
Generates grimoire-style black and white ink contour illustrations.

Supports multiple backends:
  - "nano-banana" (Fal.ai nano banana model)
  - "dall-e" (OpenAI DALL-E 3)

Usage:
    python data/generate_images.py [--backend nano-banana|dall-e]

Environment variables:
    FAL_KEY         — for nano-banana backend
    OPENAI_API_KEY  — for dall-e backend
"""

import argparse
import json
import os
import sys
from pathlib import Path

GRIMOIRE_STYLE_PREFIX = (
    "Black and white ink drawing, ancient grimoire illustration style, "
    "detailed contour lines only, no shading, no fill, no gray tones, "
    "clean linework on aged parchment paper, alchemical manuscript aesthetic, "
    "medieval occult book illustration, "
)

STATIC_DIR = Path(__file__).parent.parent / "static" / "images"


def generate_with_fal(prompt: str, aspect_ratio: str, output_path: Path) -> bool:
    """Generate image using Fal.ai (nano banana or similar fast model)."""
    try:
        import fal_client
    except ImportError:
        print("Install fal-client: pip install fal-client")
        return False

    result = fal_client.subscribe(
        "fal-ai/flux/schnell",
        arguments={
            "prompt": GRIMOIRE_STYLE_PREFIX + prompt,
            "image_size": "landscape_16_9" if aspect_ratio == "landscape" else "portrait_16_9",
            "num_images": 1,
            "enable_safety_checker": False,
        },
    )

    if result and result.get("images"):
        image_url = result["images"][0]["url"]
        import httpx

        response = httpx.get(image_url)
        output_path.parent.mkdir(parents=True, exist_ok=True)
        output_path.write_bytes(response.content)
        return True
    return False


def generate_with_dalle(prompt: str, size: str, output_path: Path) -> bool:
    """Generate image using OpenAI DALL-E 3."""
    try:
        from openai import OpenAI
    except ImportError:
        print("Install openai: pip install openai")
        return False

    client = OpenAI()
    response = client.images.generate(
        model="dall-e-3",
        prompt=GRIMOIRE_STYLE_PREFIX + prompt,
        size=size,
        quality="standard",
        n=1,
        style="natural",
    )

    if response.data:
        import httpx

        img_response = httpx.get(response.data[0].url)
        output_path.parent.mkdir(parents=True, exist_ok=True)
        output_path.write_bytes(img_response.content)
        return True
    return False


def process_entity(entity: dict, backend: str) -> dict:
    """Generate landscape and portrait images for a single entity."""
    entity_id = entity["id"]
    prompt = entity.get("image_prompt", entity.get("appearance", entity["name"]))

    landscape_path = STATIC_DIR / "landscape" / f"{entity_id}.png"
    portrait_path = STATIC_DIR / "portrait" / f"{entity_id}.png"

    results = {"entity_id": entity_id, "landscape": None, "portrait": None}

    # Landscape (16:9)
    if not landscape_path.exists():
        print(f"  [landscape] {entity['name']}...")
        if backend == "nano-banana":
            ok = generate_with_fal(prompt, "landscape", landscape_path)
        else:
            ok = generate_with_dalle(prompt, "1792x1024", landscape_path)
        if ok:
            results["landscape"] = str(landscape_path)
    else:
        print(f"  [landscape] {entity['name']} — already exists, skipping")
        results["landscape"] = str(landscape_path)

    # Portrait (9:16)
    if not portrait_path.exists():
        print(f"  [portrait]  {entity['name']}...")
        if backend == "nano-banana":
            ok = generate_with_fal(prompt, "portrait", portrait_path)
        else:
            ok = generate_with_dalle(prompt, "1024x1792", portrait_path)
        if ok:
            results["portrait"] = str(portrait_path)
    else:
        print(f"  [portrait]  {entity['name']} — already exists, skipping")
        results["portrait"] = str(portrait_path)

    return results


def main():
    parser = argparse.ArgumentParser(description="Generate grimoire images for Omnis Mytho")
    parser.add_argument(
        "--backend",
        choices=["nano-banana", "dall-e"],
        default="nano-banana",
        help="Image generation backend (default: nano-banana)",
    )
    parser.add_argument(
        "--source",
        default=None,
        help="Path to content.json (default: uses seed_data.py)",
    )
    args = parser.parse_args()

    # Validate API key
    if args.backend == "nano-banana" and not os.environ.get("FAL_KEY"):
        print("Set FAL_KEY environment variable for nano-banana backend.")
        sys.exit(1)
    if args.backend == "dall-e" and not os.environ.get("OPENAI_API_KEY"):
        print("Set OPENAI_API_KEY environment variable for dall-e backend.")
        sys.exit(1)

    # Load entities
    if args.source:
        with open(args.source, encoding="utf-8") as f:
            data = json.load(f)
        entities = data.get("entities", [])
    else:
        from seed_data import ENTITIES
        entities = [e.model_dump() for e in ENTITIES]

    print(f"Backend: {args.backend}")
    print(f"Entities: {len(entities)}")
    print(f"Output:   {STATIC_DIR}\n")

    STATIC_DIR.mkdir(parents=True, exist_ok=True)
    (STATIC_DIR / "landscape").mkdir(exist_ok=True)
    (STATIC_DIR / "portrait").mkdir(exist_ok=True)

    results = []
    for entity in entities:
        result = process_entity(entity, args.backend)
        results.append(result)

    # Save manifest
    manifest_path = STATIC_DIR / "manifest.json"
    with open(manifest_path, "w", encoding="utf-8") as f:
        json.dump(results, f, indent=2)

    generated = sum(1 for r in results if r["landscape"] or r["portrait"])
    print(f"\nDone. {generated}/{len(entities)} entities have images.")
    print(f"Manifest: {manifest_path}")


if __name__ == "__main__":
    main()
