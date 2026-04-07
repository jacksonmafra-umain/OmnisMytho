"""
Hybrid content pipeline for Omnis Mytho.
1. Scrapes factual mythology data from Wikipedia API (free, no auth)
2. Enriches with OpenAI GPT in grimoire tone
3. Generates images with Fal.ai (nano banana)

Usage:
    python data/scrape_and_generate.py [--skip-images] [--mythology greek]

Environment variables:
    OPENAI_API_KEY  — for content enrichment
    FAL_KEY         — for image generation (optional with --skip-images)
"""

import argparse
import json
import os
import re
import sys
import time
from pathlib import Path

import httpx

# ─── Wikipedia Scraping ───────────────────────────────────────────────────────

WIKIPEDIA_API = "https://en.wikipedia.org/w/api.php"

# Curated entity lists per mythology (ensures quality + relevance)
MYTHOLOGY_ENTITIES: dict[str, dict] = {
    "greek": {
        "name": "Greek Mythology",
        "origin": "Ancient Greece",
        "entities": [
            ("Zeus", "god"), ("Athena", "god"), ("Poseidon", "god"),
            ("Hades", "god"), ("Apollo", "god"), ("Artemis", "god"),
            ("Ares", "god"), ("Aphrodite", "god"), ("Hermes", "god"),
            ("Hephaestus", "god"), ("Dionysus", "god"), ("Demeter", "god"),
            ("Hecate", "god"), ("Persephone", "god"), ("Prometheus", "god"),
            ("Medusa", "creature"), ("Cerberus", "creature"), ("Minotaur", "creature"),
            ("Hydra", "creature"), ("Typhon", "creature"), ("Chimera", "creature"),
            ("Pegasus", "creature"), ("Cyclops", "creature"),
        ],
    },
    "norse": {
        "name": "Norse Mythology",
        "origin": "Scandinavia",
        "entities": [
            ("Odin", "god"), ("Thor", "god"), ("Loki", "god"),
            ("Freyja", "god"), ("Frigg", "god"), ("Tyr (deity)", "god"),
            ("Baldr", "god"), ("Heimdallr", "god"), ("Hel (mythological being)", "god"),
            ("Fenrir", "creature"), ("Jörmungandr", "creature"),
            ("Sleipnir", "creature"), ("Valkyrie", "spirit"),
            ("Draugr", "creature"), ("Nidhogg", "creature"),
        ],
    },
    "egyptian": {
        "name": "Egyptian Mythology",
        "origin": "Ancient Egypt",
        "entities": [
            ("Ra", "god"), ("Osiris", "god"), ("Isis", "god"),
            ("Anubis", "god"), ("Horus", "god"), ("Set (deity)", "god"),
            ("Thoth", "god"), ("Bastet", "god"), ("Sekhmet", "god"),
            ("Hathor", "god"), ("Sobek", "god"), ("Maat", "god"),
            ("Ammit", "creature"), ("Sphinx", "creature"),
            ("Apophis", "demon"), ("Bennu", "creature"),
        ],
    },
    "hindu": {
        "name": "Hindu Mythology",
        "origin": "Indian Subcontinent",
        "entities": [
            ("Shiva", "god"), ("Vishnu", "god"), ("Brahma", "god"),
            ("Kali", "god"), ("Ganesha", "god"), ("Hanuman", "god"),
            ("Lakshmi", "god"), ("Saraswati", "god"), ("Durga", "god"),
            ("Indra", "god"), ("Agni", "god"),
            ("Ravana", "demon"), ("Garuda", "creature"),
            ("Naga (mythology)", "creature"), ("Rakshasa", "demon"),
        ],
    },
    "japanese": {
        "name": "Japanese Mythology",
        "origin": "Japan",
        "entities": [
            ("Amaterasu", "god"), ("Susanoo", "god"), ("Tsukuyomi", "god"),
            ("Izanagi", "god"), ("Izanami", "god"), ("Raijin", "god"),
            ("Fujin", "god"), ("Inari Ōkami", "god"),
            ("Oni", "demon"), ("Kitsune", "spirit"), ("Tengu", "spirit"),
            ("Yuki-onna", "spirit"), ("Kappa (folklore)", "creature"),
            ("Jorōgumo", "creature"), ("Tanuki (folklore)", "spirit"),
        ],
    },
    "christian": {
        "name": "Christian Demonology & Angelology",
        "origin": "Abrahamic Tradition",
        "entities": [
            ("Michael (archangel)", "angel"), ("Gabriel", "angel"),
            ("Raphael (archangel)", "angel"), ("Uriel", "angel"),
            ("Azrael", "angel"), ("Metatron", "angel"),
            ("Lucifer", "demon"), ("Beelzebub", "demon"),
            ("Asmodeus", "demon"), ("Lilith", "demon"),
            ("Azazel", "demon"), ("Baphomet", "demon"),
            ("Leviathan", "creature"), ("Behemoth", "creature"),
        ],
    },
}


WIKI_HEADERS = {
    "User-Agent": "OmnisMytho/1.0 (https://github.com/omnismytho; educational mythology project)",
}


def fetch_wikipedia_extract(title: str) -> str:
    """Fetch the opening extract of a Wikipedia article."""
    params = {
        "action": "query",
        "titles": title,
        "prop": "extracts",
        "exintro": True,
        "explaintext": True,
        "format": "json",
        "redirects": 1,
    }
    try:
        resp = httpx.get(WIKIPEDIA_API, params=params, headers=WIKI_HEADERS, timeout=15)
        data = resp.json()
        pages = data.get("query", {}).get("pages", {})
        for page in pages.values():
            extract = page.get("extract", "")
            if extract:
                # Clean up: remove pronunciation guides, parenthetical noise
                extract = re.sub(r'\([^)]*pronunciation[^)]*\)', '', extract)
                extract = re.sub(r'\([^)]*listen[^)]*\)', '', extract)
                extract = re.sub(r'\s+', ' ', extract).strip()
                return extract[:2000]  # Cap at 2000 chars
        return ""
    except Exception as e:
        print(f"    Wikipedia fetch failed for '{title}': {e}")
        return ""


def fetch_wikipedia_image(title: str) -> str:
    """Fetch the main image URL from Wikipedia."""
    params = {
        "action": "query",
        "titles": title,
        "prop": "pageimages",
        "piprop": "original",
        "format": "json",
        "redirects": 1,
    }
    try:
        resp = httpx.get(WIKIPEDIA_API, params=params, headers=WIKI_HEADERS, timeout=15)
        data = resp.json()
        pages = data.get("query", {}).get("pages", {})
        for page in pages.values():
            original = page.get("original", {})
            return original.get("source", "")
        return ""
    except Exception:
        return ""


def scrape_mythology(mythology_id: str) -> dict:
    """Scrape all entities for a mythology from Wikipedia."""
    config = MYTHOLOGY_ENTITIES[mythology_id]
    print(f"\n{'='*60}")
    print(f"Scraping: {config['name']}")
    print(f"{'='*60}")

    scraped_entities = []
    for entity_name, entity_type in config["entities"]:
        # Use the article title for Wikipedia, but clean name for display
        display_name = entity_name.split(" (")[0]  # Remove disambiguation
        print(f"  Fetching: {display_name}...")

        extract = fetch_wikipedia_extract(entity_name)
        if not extract:
            print(f"    -> No Wikipedia article found, skipping")
            continue

        scraped_entities.append({
            "wikipedia_title": entity_name,
            "name": display_name,
            "type": entity_type,
            "wikipedia_extract": extract,
        })

        time.sleep(0.5)  # Be polite to Wikipedia API

    print(f"  -> Scraped {len(scraped_entities)} entities")
    return {
        "mythology_id": mythology_id,
        "name": config["name"],
        "origin": config["origin"],
        "entities": scraped_entities,
    }


# ─── LLM Enrichment ──────────────────────────────────────────────────────────

ENRICH_SYSTEM = """You are an ancient scholar writing entries for a forbidden grimoire called "Omnis Mytho".

Your task: take factual Wikipedia data about mythological entities and rewrite it in the voice of a medieval occult encyclopaedia.

Rules:
- Tone: archaic, mystical, reverent yet ominous
- Keep factual accuracy but add evocative, dark atmosphere
- Descriptions: 2-3 sentences max, dense with imagery
- Appearance: detailed visual description suitable for a black and white ink illustration
- Powers: extract from the source material, list 3-5 specific abilities
- Symbols: extract associated objects, animals, elements (3-5 items)
- Personality: 1 sentence capturing their essential nature
- Alignment: classify as good/neutral/evil/chaotic based on mythology
- image_prompt: describe for AI art — "Black and white ink contour drawing, grimoire style, [specific visual details]"
- Title: a short epithet (e.g. "God of Thunder", "The Trickster", "Lord of the Dead")"""

ENRICH_USER = """Based on this Wikipedia data, create a grimoire entry.

Entity: {name}
Type: {type}
Wikipedia text:
{extract}

Return ONLY valid JSON:
{{
  "title": "short epithet",
  "description": "2-3 grimoire-style sentences",
  "appearance": "detailed visual description for illustration",
  "powers": ["power1", "power2", "power3"],
  "symbols": ["symbol1", "symbol2", "symbol3"],
  "personality": "one sentence",
  "alignment": "good|neutral|evil|chaotic",
  "image_prompt": "Black and white ink contour drawing, grimoire illustration style, detailed linework, parchment aesthetic, [entity-specific details]"
}}"""


def enrich_entity(entity: dict, client) -> dict:
    """Use OpenAI to transform Wikipedia data into grimoire entry."""
    try:
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {"role": "system", "content": ENRICH_SYSTEM},
                {"role": "user", "content": ENRICH_USER.format(
                    name=entity["name"],
                    type=entity["type"],
                    extract=entity["wikipedia_extract"],
                )},
            ],
            temperature=0.8,
            max_tokens=800,
            response_format={"type": "json_object"},
        )

        text = response.choices[0].message.content
        enriched = json.loads(text)
        return enriched
    except Exception as e:
        print(f"    LLM enrichment failed: {e}")
        return None


# ─── Image Generation ─────────────────────────────────────────────────────────

STATIC_DIR = Path(__file__).parent.parent / "static" / "images"

GRIMOIRE_STYLE = (
    "Black and white ink drawing, ancient grimoire illustration style, "
    "detailed contour lines only, no shading, no fill, no gray tones, "
    "clean linework on aged parchment paper, alchemical manuscript aesthetic, "
    "medieval occult book illustration, "
)


def generate_image_fal(prompt: str, aspect_ratio: str, output_path: Path) -> bool:
    """Generate image using Fal.ai."""
    try:
        import fal_client
    except ImportError:
        print("    Install fal-client: pip install fal-client")
        return False

    try:
        result = fal_client.subscribe(
            "fal-ai/flux/schnell",
            arguments={
                "prompt": GRIMOIRE_STYLE + prompt,
                "image_size": "landscape_16_9" if aspect_ratio == "landscape" else "portrait_16_9",
                "num_images": 1,
                "enable_safety_checker": False,
            },
        )

        if result and result.get("images"):
            image_url = result["images"][0]["url"]
            resp = httpx.get(image_url, timeout=30)
            output_path.parent.mkdir(parents=True, exist_ok=True)
            output_path.write_bytes(resp.content)
            return True
    except Exception as e:
        print(f"    Image generation failed: {e}")
    return False


def generate_images_for_entity(entity_id: str, prompt: str) -> dict:
    """Generate landscape and portrait images."""
    landscape = STATIC_DIR / "landscape" / f"{entity_id}.png"
    portrait = STATIC_DIR / "portrait" / f"{entity_id}.png"

    result = {"entity_id": entity_id, "landscape": None, "portrait": None}

    if not landscape.exists():
        print(f"    [landscape] generating...")
        if generate_image_fal(prompt, "landscape", landscape):
            result["landscape"] = f"/static/images/landscape/{entity_id}.png"
    else:
        result["landscape"] = f"/static/images/landscape/{entity_id}.png"

    if not portrait.exists():
        print(f"    [portrait] generating...")
        if generate_image_fal(prompt, "portrait", portrait):
            result["portrait"] = f"/static/images/portrait/{entity_id}.png"
    else:
        result["portrait"] = f"/static/images/portrait/{entity_id}.png"

    return result


# ─── Main Pipeline ────────────────────────────────────────────────────────────

def main():
    parser = argparse.ArgumentParser(description="Hybrid scrape + LLM pipeline for Omnis Mytho")
    parser.add_argument("--skip-images", action="store_true", help="Skip image generation")
    parser.add_argument("--mythology", type=str, default=None,
                        help="Generate only one mythology (e.g. 'greek')")
    parser.add_argument("--output", type=str, default=None,
                        help="Output JSON path (default: data/generated/content.json)")
    args = parser.parse_args()

    # Validate keys
    if not os.environ.get("OPENAI_API_KEY"):
        print("Set OPENAI_API_KEY environment variable.")
        sys.exit(1)

    if not args.skip_images and not os.environ.get("FAL_KEY"):
        print("Set FAL_KEY for image generation, or use --skip-images.")
        sys.exit(1)

    from openai import OpenAI
    client = OpenAI()

    # Select mythologies
    if args.mythology:
        if args.mythology not in MYTHOLOGY_ENTITIES:
            print(f"Unknown mythology: {args.mythology}")
            print(f"Available: {', '.join(MYTHOLOGY_ENTITIES.keys())}")
            sys.exit(1)
        mythology_ids = [args.mythology]
    else:
        mythology_ids = list(MYTHOLOGY_ENTITIES.keys())

    # Prepare output
    output_dir = Path(__file__).parent / "generated"
    output_dir.mkdir(exist_ok=True)
    STATIC_DIR.mkdir(parents=True, exist_ok=True)
    (STATIC_DIR / "landscape").mkdir(exist_ok=True)
    (STATIC_DIR / "portrait").mkdir(exist_ok=True)

    all_data = {"mythologies": [], "entities": []}

    for myth_id in mythology_ids:
        # Step 1: Scrape Wikipedia
        scraped = scrape_mythology(myth_id)

        # Build mythology entry
        mythology_entry = {
            "id": myth_id,
            "name": scraped["name"],
            "origin": scraped["origin"],
            "description": "",  # Will be enriched
            "entity_count": len(scraped["entities"]),
        }

        # Enrich mythology description
        print(f"\n  Enriching mythology description...")
        try:
            resp = client.chat.completions.create(
                model="gpt-4o-mini",
                messages=[
                    {"role": "system", "content": ENRICH_SYSTEM},
                    {"role": "user", "content": f"Write a 2-sentence grimoire-style description for {scraped['name']} ({scraped['origin']}). Return ONLY the text, no JSON."},
                ],
                temperature=0.8,
                max_tokens=200,
            )
            mythology_entry["description"] = resp.choices[0].message.content.strip()
        except Exception as e:
            mythology_entry["description"] = f"The ancient tradition of {scraped['name']}."
            print(f"    Description enrichment failed: {e}")

        all_data["mythologies"].append(mythology_entry)

        # Step 2: Enrich each entity with LLM
        print(f"\n  Enriching {len(scraped['entities'])} entities with LLM...")
        for i, raw_entity in enumerate(scraped["entities"]):
            entity_name = raw_entity["name"]
            entity_id = f"{myth_id}-{entity_name.lower().replace(' ', '-').replace('ö', 'o').replace('ō', 'o').replace('ū', 'u')}-{i+1:03d}"

            print(f"\n  [{i+1}/{len(scraped['entities'])}] {entity_name}")
            print(f"    Enriching with LLM...")

            enriched = enrich_entity(raw_entity, client)
            if not enriched:
                print(f"    -> Skipping (enrichment failed)")
                continue

            entity_entry = {
                "id": entity_id,
                "name": entity_name,
                "type": raw_entity["type"],
                "mythology_id": myth_id,
                "title": enriched.get("title", ""),
                "description": enriched.get("description", ""),
                "appearance": enriched.get("appearance", ""),
                "powers": enriched.get("powers", []),
                "symbols": enriched.get("symbols", []),
                "personality": enriched.get("personality", ""),
                "alignment": enriched.get("alignment", "neutral"),
                "image_prompt": enriched.get("image_prompt", ""),
            }

            # Step 3: Generate images
            if not args.skip_images:
                print(f"    Generating images...")
                img_result = generate_images_for_entity(
                    entity_id,
                    enriched.get("image_prompt", enriched.get("appearance", entity_name)),
                )
                entity_entry["landscape_image"] = img_result.get("landscape", "")
                entity_entry["portrait_image"] = img_result.get("portrait", "")

            all_data["entities"].append(entity_entry)
            time.sleep(0.3)  # Rate limiting

    # Save output
    output_path = Path(args.output) if args.output else output_dir / "content.json"
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(all_data, f, indent=2, ensure_ascii=False)

    print(f"\n{'='*60}")
    print(f"Pipeline complete!")
    print(f"{'='*60}")
    print(f"Mythologies: {len(all_data['mythologies'])}")
    print(f"Entities:    {len(all_data['entities'])}")
    print(f"Output:      {output_path}")

    if not args.skip_images:
        img_count = sum(1 for e in all_data["entities"]
                       if e.get("landscape_image") or e.get("portrait_image"))
        print(f"Images:      {img_count} entities with images")


if __name__ == "__main__":
    main()
