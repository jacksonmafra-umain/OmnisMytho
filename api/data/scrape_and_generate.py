"""
Hybrid content pipeline for Omnis Mytho.
1. Scrapes factual mythology data from Wikipedia API (free, no auth)
2. Enriches with LLM in grimoire tone (OpenAI primary, Gemini fallback)
3. Generates images with Fal.ai (nano banana)

Usage:
    python data/scrape_and_generate.py [--skip-images] [--mythology greek] [--llm openai|gemini]

Environment variables:
    OPENAI_API_KEY    — for OpenAI (primary)
    GEMINI_API_KEY    — for Google Gemini (fallback)
    FAL_KEY           — for image generation (optional with --skip-images)
"""

import argparse
import json
import os
import re
import sys
import time
from pathlib import Path

import httpx

# Auto-load .env if present
_env_path = Path(__file__).parent.parent / ".env"
if _env_path.exists():
    for line in _env_path.read_text().splitlines():
        line = line.strip()
        if line and not line.startswith("#") and "=" in line:
            key, _, value = line.partition("=")
            os.environ.setdefault(key.strip(), value.strip())

# ─── Wikipedia Scraping ───────────────────────────────────────────────────────

WIKIPEDIA_API = "https://en.wikipedia.org/w/api.php"

WIKI_HEADERS = {
    "User-Agent": "OmnisMytho/1.0 (https://github.com/omnismytho; educational mythology project)",
}

# Curated entity lists per mythology
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
    "roman": {
        "name": "Roman Mythology",
        "origin": "Ancient Rome",
        "entities": [
            ("Jupiter (mythology)", "god"), ("Mars (mythology)", "god"),
            ("Venus (mythology)", "god"), ("Neptune (mythology)", "god"),
            ("Minerva", "god"), ("Diana (mythology)", "god"),
            ("Mercury (mythology)", "god"), ("Pluto (mythology)", "god"),
            ("Juno (mythology)", "god"), ("Saturn (mythology)", "god"),
            ("Vulcan (mythology)", "god"), ("Bacchus", "god"),
            ("Janus", "god"), ("Romulus and Remus", "creature"),
        ],
    },
    "celtic": {
        "name": "Celtic Mythology",
        "origin": "British Isles & Gaul",
        "entities": [
            ("The Dagda", "god"), ("Brigid", "god"), ("Lugh", "god"),
            ("Morrígan", "god"), ("Cernunnos", "god"), ("Manannán mac Lir", "god"),
            ("Danu (Irish goddess)", "god"), ("Aengus", "god"),
            ("Cú Chulainn", "creature"), ("Fionn mac Cumhaill", "creature"),
            ("Banshee", "spirit"), ("Dullahan", "spirit"),
            ("Balor", "demon"), ("Púca", "spirit"),
        ],
    },
    "mesopotamian": {
        "name": "Mesopotamian Mythology",
        "origin": "Sumer, Babylon & Assyria",
        "entities": [
            ("Anu (god)", "god"), ("Enlil", "god"), ("Enki", "god"),
            ("Inanna", "god"), ("Marduk", "god"), ("Ishtar", "god"),
            ("Tiamat", "creature"), ("Ereshkigal", "god"),
            ("Nergal", "god"), ("Shamash", "god"),
            ("Gilgamesh", "creature"), ("Enkidu", "creature"),
            ("Pazuzu", "demon"), ("Lamashtu", "demon"),
        ],
    },
    "chinese": {
        "name": "Chinese Mythology",
        "origin": "China",
        "entities": [
            ("Jade Emperor", "god"), ("Sun Wukong", "god"),
            ("Nüwa", "god"), ("Fuxi", "god"),
            ("Guanyin", "god"), ("Erlang Shen", "god"),
            ("Nezha", "god"), ("Dragon King", "god"),
            ("Chang'e", "god"), ("Zhong Kui", "spirit"),
            ("Chinese dragon", "creature"), ("Fenghuang", "creature"),
            ("Qilin", "creature"), ("Yanluo Wang", "god"),
        ],
    },
    "slavic": {
        "name": "Slavic Mythology",
        "origin": "Eastern Europe",
        "entities": [
            ("Perun", "god"), ("Veles (god)", "god"), ("Svarog", "god"),
            ("Morana (goddess)", "god"), ("Stribog", "god"), ("Dazhbog", "god"),
            ("Rod (Slavic religion)", "god"),
            ("Baba Yaga", "creature"), ("Domovoy", "spirit"),
            ("Rusalka", "spirit"), ("Leshy", "spirit"),
            ("Koschei", "demon"), ("Zmey Gorynych", "creature"),
        ],
    },
    "aztec": {
        "name": "Aztec Mythology",
        "origin": "Mesoamerica",
        "entities": [
            ("Quetzalcoatl", "god"), ("Tezcatlipoca", "god"),
            ("Huitzilopochtli", "god"), ("Tlaloc", "god"),
            ("Xipe Totec", "god"), ("Mictlantecuhtli", "god"),
            ("Coatlicue", "god"), ("Chalchiuhtlicue", "god"),
            ("Xolotl", "god"), ("Tonatiuh", "god"),
            ("Ahuizotl (creature)", "creature"), ("Cipactli", "creature"),
        ],
    },
    "mayan": {
        "name": "Mayan Mythology",
        "origin": "Mesoamerica",
        "entities": [
            ("Itzamna", "god"), ("Kukulkan", "god"),
            ("Ix Chel", "god"), ("Chaac", "god"),
            ("Ah Puch", "god"), ("Hunahpu", "god"),
            ("Xbalanque", "god"), ("Hun Hunahpu", "god"),
            ("Zipacna", "creature"), ("Camazotz", "demon"),
        ],
    },
    "incan": {
        "name": "Incan Mythology",
        "origin": "South America (Andes)",
        "entities": [
            ("Inti", "god"), ("Viracocha", "god"),
            ("Pachamama", "god"), ("Mama Quilla", "god"),
            ("Supay", "demon"), ("Illapa", "god"),
            ("Kon (mythology)", "god"), ("Mama Cocha", "god"),
            ("Catequil", "god"), ("Ekeko", "god"),
        ],
    },
    "polynesian": {
        "name": "Polynesian Mythology",
        "origin": "Polynesia & Oceania",
        "entities": [
            ("Māui (mythology)", "god"), ("Tāne", "god"),
            ("Tangaroa", "god"), ("Tū (Māori god)", "god"),
            ("Pele (deity)", "god"), ("Rongo", "god"),
            ("Hina (goddess)", "god"), ("Whiro", "demon"),
            ("Patupaiarehe", "spirit"), ("Menehune", "spirit"),
        ],
    },
    "aboriginal": {
        "name": "Aboriginal Australian Mythology",
        "origin": "Australia (Dreamtime)",
        "entities": [
            ("Rainbow Serpent", "god"), ("Baiame", "god"),
            ("Yowie", "creature"), ("Tiddalik", "creature"),
            ("Bunyip", "creature"), ("Wollunqua", "creature"),
            ("Altjira", "god"), ("Djanggawul", "spirit"),
            ("Mimi (folklore)", "spirit"),
        ],
    },
    "yoruba": {
        "name": "Yoruba & West African Mythology",
        "origin": "West Africa (Nigeria)",
        "entities": [
            ("Olorun", "god"), ("Obatala", "god"),
            ("Shango", "god"), ("Ogun", "god"),
            ("Yemoja", "god"), ("Oshun", "god"),
            ("Eshu", "god"), ("Oya (deity)", "god"),
            ("Orunmila", "god"), ("Anansi", "spirit"),
            ("Mami Wata", "spirit"), ("Abiku", "demon"),
        ],
    },
    "vodou": {
        "name": "Vodou & Caribbean Syncretic Mythology",
        "origin": "Haiti & Caribbean",
        "entities": [
            ("Papa Legba", "god"), ("Baron Samedi", "god"),
            ("Maman Brigitte", "god"), ("Erzulie", "god"),
            ("Damballa", "god"), ("Agwé", "god"),
            ("Marinette (loa)", "demon"), ("Kalfu", "demon"),
            ("Loco (loa)", "spirit"), ("Gran Bwa", "spirit"),
        ],
    },
    "persian": {
        "name": "Persian / Zoroastrian Mythology",
        "origin": "Ancient Persia (Iran)",
        "entities": [
            ("Ahura Mazda", "god"), ("Angra Mainyu", "demon"),
            ("Mithra", "god"), ("Anahita", "god"),
            ("Rashnu", "god"), ("Sraosha", "angel"),
            ("Atar", "god"), ("Verethragna", "god"),
            ("Simurgh", "creature"), ("Azhi Dahaka", "demon"),
            ("Div (mythology)", "demon"), ("Huma bird", "creature"),
        ],
    },
    "korean": {
        "name": "Korean Mythology",
        "origin": "Korea",
        "entities": [
            ("Hwanung", "god"), ("Dangun", "god"),
            ("Habaek", "god"), ("Jacheongbi", "god"),
            ("Dokkaebi", "spirit"), ("Gumiho", "demon"),
            ("Jeoseung Saja", "spirit"), ("Imoogi", "creature"),
            ("Samjogo", "creature"), ("Bulgasari", "creature"),
        ],
    },
    "tibetan": {
        "name": "Tibetan & Bön Mythology",
        "origin": "Tibet & Himalaya",
        "entities": [
            ("Padmasambhava", "god"), ("Palden Lhamo", "god"),
            ("Vajrapani", "god"), ("Yamantaka", "god"),
            ("Mahakala", "god"), ("Tara (Buddhism)", "god"),
            ("Yeti", "creature"), ("Druk (mythology)", "creature"),
            ("Gyalpo (spirit)", "demon"), ("Nyen", "spirit"),
        ],
    },
    "finnish": {
        "name": "Finnish & Kalevala Mythology",
        "origin": "Finland & Karelia",
        "entities": [
            ("Ukko", "god"), ("Väinämöinen", "god"),
            ("Louhi", "god"), ("Ilmarinen", "god"),
            ("Lemminkäinen", "god"), ("Tapio (spirit)", "god"),
            ("Tuoni", "god"), ("Mielikki", "god"),
            ("Näkki", "spirit"), ("Hiisi", "demon"),
        ],
    },
    "philippine": {
        "name": "Philippine Mythology",
        "origin": "Philippines",
        "entities": [
            ("Bathala", "god"), ("Mayari", "god"),
            ("Tala (goddess)", "god"), ("Apolaki", "god"),
            ("Dian Masalanta", "god"), ("Aswang", "demon"),
            ("Tikbalang", "creature"), ("Diwata", "spirit"),
            ("Bakunawa", "creature"), ("Kapre", "spirit"),
        ],
    },
    "native_american": {
        "name": "Native North American Mythology",
        "origin": "North America",
        "entities": [
            ("Coyote (mythology)", "spirit"), ("Raven (mythology)", "spirit"),
            ("Thunderbird (mythology)", "spirit"), ("Kokopelli", "spirit"),
            ("Wendigo", "demon"), ("Skinwalker", "demon"),
            ("Spider Grandmother", "god"), ("Great Spirit", "god"),
            ("White Buffalo Calf Woman", "spirit"), ("Deer Woman", "spirit"),
        ],
    },
    "indonesian": {
        "name": "Indonesian & Malay Mythology",
        "origin": "Southeast Asia",
        "entities": [
            ("Batara Guru", "god"), ("Dewi Sri", "god"),
            ("Nyai Roro Kidul", "god"), ("Rangda", "demon"),
            ("Barong (mythology)", "creature"), ("Garuda", "creature"),
            ("Pontianak (folklore)", "demon"), ("Penanggalan", "demon"),
            ("Jenglot", "creature"), ("Naga (mythology)", "creature"),
        ],
    },
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
                extract = re.sub(r'\([^)]*pronunciation[^)]*\)', '', extract)
                extract = re.sub(r'\([^)]*listen[^)]*\)', '', extract)
                extract = re.sub(r'\s+', ' ', extract).strip()
                return extract[:2000]
        return ""
    except Exception as e:
        print(f"    Wikipedia fetch failed for '{title}': {e}")
        return ""


def scrape_mythology(mythology_id: str) -> dict:
    """Scrape all entities for a mythology from Wikipedia."""
    config = MYTHOLOGY_ENTITIES[mythology_id]
    print(f"\n{'='*60}")
    print(f"Scraping: {config['name']}")
    print(f"{'='*60}")

    scraped_entities = []
    for entity_name, entity_type in config["entities"]:
        display_name = entity_name.split(" (")[0]
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

        time.sleep(0.5)

    print(f"  -> Scraped {len(scraped_entities)} entities")
    return {
        "mythology_id": mythology_id,
        "name": config["name"],
        "origin": config["origin"],
        "entities": scraped_entities,
    }


# ─── LLM Client Abstraction ──────────────────────────────────────────────────

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


class LLMClient:
    """Unified LLM client with OpenAI primary, Gemini fallback."""

    def __init__(self, preferred: str = "auto"):
        self.openai_client = None
        self.gemini_client = None
        self.active = None

        if preferred in ("auto", "openai") and os.environ.get("OPENAI_API_KEY"):
            try:
                from openai import OpenAI
                self.openai_client = OpenAI()
                self.active = "openai"
                print(f"  LLM: OpenAI (primary)")
            except ImportError:
                print("  OpenAI SDK not installed (pip install openai)")

        if preferred in ("auto", "gemini") and os.environ.get("GEMINI_API_KEY"):
            try:
                from google import genai
                self.gemini_client = genai.Client(api_key=os.environ["GEMINI_API_KEY"])
                if not self.active:
                    self.active = "gemini"
                    print(f"  LLM: Gemini (primary)")
                else:
                    print(f"  LLM: Gemini (fallback)")
            except ImportError:
                print("  Gemini SDK not installed (pip install google-genai)")

        if preferred == "openai" and self.openai_client:
            self.active = "openai"
        elif preferred == "gemini" and self.gemini_client:
            self.active = "gemini"

        if not self.openai_client and not self.gemini_client:
            print("\nNo LLM available. Set OPENAI_API_KEY or GEMINI_API_KEY.")
            sys.exit(1)

    def chat(self, system: str, user: str, json_mode: bool = False) -> str:
        """Send a chat request, with automatic fallback."""
        # Try primary
        result = self._call(self.active, system, user, json_mode)
        if result:
            return result

        # Fallback
        fallback = "gemini" if self.active == "openai" else "openai"
        if (fallback == "openai" and self.openai_client) or \
           (fallback == "gemini" and self.gemini_client):
            print(f"    -> Falling back to {fallback}...")
            result = self._call(fallback, system, user, json_mode)
            if result:
                return result

        return ""

    def _call(self, provider: str, system: str, user: str, json_mode: bool) -> str:
        try:
            if provider == "openai" and self.openai_client:
                return self._call_openai(system, user, json_mode)
            elif provider == "gemini" and self.gemini_client:
                return self._call_gemini(system, user, json_mode)
        except Exception as e:
            print(f"    {provider} error: {e}")
        return ""

    def _call_openai(self, system: str, user: str, json_mode: bool) -> str:
        kwargs = {
            "model": "gpt-4o-mini",
            "messages": [
                {"role": "system", "content": system},
                {"role": "user", "content": user},
            ],
            "temperature": 0.8,
            "max_tokens": 800,
        }
        if json_mode:
            kwargs["response_format"] = {"type": "json_object"}

        response = self.openai_client.chat.completions.create(**kwargs)
        return response.choices[0].message.content

    def _call_gemini(self, system: str, user: str, json_mode: bool) -> str:
        from google.genai import types

        config = types.GenerateContentConfig(
            system_instruction=system,
            temperature=0.8,
            max_output_tokens=800,
        )
        if json_mode:
            config.response_mime_type = "application/json"

        response = self.gemini_client.models.generate_content(
            model="gemini-2.0-flash",
            contents=user,
            config=config,
        )
        return response.text


def enrich_entity_from_wikipedia(entity: dict) -> dict:
    """Create a grimoire-style entry from Wikipedia data alone (no LLM needed)."""
    name = entity["name"]
    etype = entity["type"]
    extract = entity["wikipedia_extract"]

    # Take first 2 sentences as description
    sentences = re.split(r'(?<=[.!?])\s+', extract)
    description = " ".join(sentences[:2]).strip() if sentences else f"A {etype} of ancient legend."

    # Extract a title from the first sentence
    title = ""
    first = sentences[0] if sentences else ""
    # Pattern: "X is the god of Y" or "X is a Y in Z mythology"
    m = re.search(r'is (?:the |a |an )?(.+?)(?:\.|,| in | who | and )', first, re.IGNORECASE)
    if m:
        title = m.group(1).strip().rstrip(",.")[:60]
    if not title:
        title = f"{etype.capitalize()} of legend"

    # Build image prompt
    image_prompt = (
        f"Black and white ink contour drawing, grimoire illustration style, "
        f"detailed linework, parchment aesthetic, {name}, {title}, "
        f"ancient manuscript illustration"
    )

    return {
        "title": title.title() if len(title) < 40 else title,
        "description": description,
        "appearance": f"Ancient depiction of {name}, {title}.",
        "powers": [],
        "symbols": [],
        "personality": "",
        "alignment": "neutral",
        "image_prompt": image_prompt,
    }


def enrich_entity(entity: dict, llm: LLMClient) -> dict:
    """Use LLM to transform Wikipedia data into grimoire entry."""
    text = llm.chat(
        system=ENRICH_SYSTEM,
        user=ENRICH_USER.format(
            name=entity["name"],
            type=entity["type"],
            extract=entity["wikipedia_extract"],
        ),
        json_mode=True,
    )

    if not text:
        return None

    try:
        # Clean potential markdown fences
        if "```json" in text:
            text = text.split("```json")[1].split("```")[0]
        elif "```" in text:
            text = text.split("```")[1].split("```")[0]
        return json.loads(text.strip())
    except json.JSONDecodeError as e:
        print(f"    JSON parse error: {e}")
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
    parser.add_argument("--no-llm", action="store_true",
                        help="Skip LLM enrichment — use Wikipedia data directly (no API key needed)")
    parser.add_argument("--mythology", type=str, default=None,
                        help="Generate only one mythology (e.g. 'greek')")
    parser.add_argument("--llm", type=str, choices=["auto", "openai", "gemini"], default="auto",
                        help="LLM provider: auto (OpenAI primary, Gemini fallback), openai, or gemini")
    parser.add_argument("--output", type=str, default=None,
                        help="Output JSON path (default: data/generated/content.json)")
    args = parser.parse_args()

    if not args.skip_images and not os.environ.get("FAL_KEY"):
        print("Set FAL_KEY for image generation, or use --skip-images.")
        sys.exit(1)

    # Initialize LLM (or skip)
    llm = None
    if not args.no_llm:
        llm = LLMClient(preferred=args.llm)
    else:
        print("  LLM: disabled (--no-llm) — using Wikipedia data directly")

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

        mythology_entry = {
            "id": myth_id,
            "name": scraped["name"],
            "origin": scraped["origin"],
            "description": "",
            "entity_count": len(scraped["entities"]),
        }

        # Enrich mythology description
        if llm:
            print(f"\n  Enriching mythology description...")
            desc = llm.chat(
                system=ENRICH_SYSTEM,
                user=f"Write a 2-sentence grimoire-style description for {scraped['name']} ({scraped['origin']}). Return ONLY the text, no JSON.",
            )
            mythology_entry["description"] = desc.strip() if desc else f"The ancient tradition of {scraped['name']}."
        else:
            mythology_entry["description"] = f"The ancient tradition of {scraped['name']}, from the lands of {scraped['origin']}."

        all_data["mythologies"].append(mythology_entry)

        # Step 2: Enrich each entity
        mode = "LLM" if llm else "Wikipedia"
        print(f"\n  Enriching {len(scraped['entities'])} entities with {mode}...")
        for i, raw_entity in enumerate(scraped["entities"]):
            entity_name = raw_entity["name"]
            entity_id = f"{myth_id}-{entity_name.lower().replace(' ', '-').replace('ö', 'o').replace('ō', 'o').replace('ū', 'u')}-{i+1:03d}"

            print(f"  [{i+1}/{len(scraped['entities'])}] {entity_name}")

            if llm:
                enriched = enrich_entity(raw_entity, llm)
            else:
                enriched = enrich_entity_from_wikipedia(raw_entity)

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
            time.sleep(0.3)

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
