"""
Content generation script using Claude API.
Generates structured JSON for mythological entities.

Usage:
    python data/generate_content.py

Requires ANTHROPIC_API_KEY environment variable.
"""

import json
import os
import sys

SYSTEM_PROMPT = """You are a mythology expert and creative writer for an ancient grimoire-style encyclopaedia app called "Omnis Mytho".

Generate structured JSON for mythological entities following these rules:
- Tone: ancient, mystical, slightly dark — like an old grimoire
- Avoid modern language
- Each entity must feel iconic and visually distinctive
- image_prompt must be detailed enough for AI image generation in black and white ink contour style
- Descriptions should be 2-3 sentences, evocative and concise"""

USER_PROMPT = """Generate a complete dataset for the following mythology: {mythology}

Return JSON with this exact structure:
{{
  "mythology": {{
    "id": "lowercase_id",
    "name": "Full Name",
    "origin": "Geographic origin",
    "description": "Short evocative paragraph"
  }},
  "entities": [
    {{
      "id": "unique_id",
      "name": "Entity Name",
      "type": "god | demon | angel | spirit | creature",
      "title": "Short epithet",
      "description": "2-3 sentences in grimoire tone",
      "appearance": "Detailed visual description",
      "powers": ["power1", "power2"],
      "symbols": ["symbol1", "symbol2"],
      "personality": "Short personality sketch",
      "alignment": "good | neutral | evil | chaotic",
      "image_prompt": "Black and white ink drawing, ancient grimoire illustration style, detailed contour lines, no fill, parchment paper aesthetic, [entity-specific details]"
    }}
  ]
}}

Include 6-8 entities per mythology. Output only valid JSON, no explanations."""


def generate_mythology(mythology_name: str) -> dict:
    try:
        import anthropic
    except ImportError:
        print("Install anthropic: pip install anthropic")
        sys.exit(1)

    client = anthropic.Anthropic()
    message = client.messages.create(
        model="claude-sonnet-4-20250514",
        max_tokens=4096,
        system=SYSTEM_PROMPT,
        messages=[
            {"role": "user", "content": USER_PROMPT.format(mythology=mythology_name)}
        ],
    )

    text = message.content[0].text
    # Extract JSON from response
    if "```json" in text:
        text = text.split("```json")[1].split("```")[0]
    elif "```" in text:
        text = text.split("```")[1].split("```")[0]

    return json.loads(text.strip())


def main():
    mythologies = [
        "Greek Mythology",
        "Norse Mythology",
        "Egyptian Mythology",
        "Hindu Mythology",
        "Japanese Mythology (Shinto & Buddhist)",
        "Christian Demonology & Angelology",
    ]

    if not os.environ.get("ANTHROPIC_API_KEY"):
        print("Set ANTHROPIC_API_KEY environment variable first.")
        print("Example: export ANTHROPIC_API_KEY=sk-ant-...")
        sys.exit(1)

    output_dir = os.path.join(os.path.dirname(__file__), "generated")
    os.makedirs(output_dir, exist_ok=True)

    all_data = {"mythologies": [], "entities": []}

    for myth in mythologies:
        print(f"Generating: {myth}...")
        try:
            data = generate_mythology(myth)
            all_data["mythologies"].append(data["mythology"])
            for entity in data["entities"]:
                entity["mythology_id"] = data["mythology"]["id"]
                all_data["entities"].append(entity)
            print(f"  -> {len(data['entities'])} entities generated")
        except Exception as e:
            print(f"  -> Error: {e}")

    output_path = os.path.join(output_dir, "content.json")
    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(all_data, f, indent=2, ensure_ascii=False)

    print(f"\nSaved to {output_path}")
    print(f"Total: {len(all_data['mythologies'])} mythologies, {len(all_data['entities'])} entities")


if __name__ == "__main__":
    main()
