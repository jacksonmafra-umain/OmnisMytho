from __future__ import annotations

from enum import Enum
from pydantic import BaseModel, Field


class EntityType(str, Enum):
    GOD = "god"
    DEMON = "demon"
    ANGEL = "angel"
    SPIRIT = "spirit"
    CREATURE = "creature"


class Alignment(str, Enum):
    GOOD = "good"
    NEUTRAL = "neutral"
    EVIL = "evil"
    CHAOTIC = "chaotic"


class Entity(BaseModel):
    """A mythological entity (god, demon, angel, spirit or creature)."""

    id: str = Field(..., description="Unique identifier", examples=["zeus-001"])
    name: str = Field(..., description="Entity name", examples=["Zeus"])
    type: EntityType = Field(..., description="Classification")
    title: str = Field(..., description="Short epithet", examples=["God of Thunder"])
    description: str = Field(
        ...,
        description="2-3 sentence evocative description in grimoire tone",
    )
    appearance: str = Field(
        ...,
        description="Visual description for image generation",
    )
    powers: list[str] = Field(default_factory=list, description="List of powers")
    symbols: list[str] = Field(default_factory=list, description="Associated symbols")
    personality: str = Field("", description="Short personality sketch")
    alignment: Alignment = Field(Alignment.NEUTRAL, description="Moral alignment")
    mythology_id: str = Field(
        ...,
        description="ID of parent mythology",
        examples=["greek"],
    )
    image_prompt: str = Field(
        "",
        description="Detailed prompt for grimoire-style image generation",
    )


class EntitySummary(BaseModel):
    """Lightweight entity for list views."""

    id: str
    name: str
    type: EntityType
    title: str
    alignment: Alignment
    mythology_id: str


class Mythology(BaseModel):
    """A mythology / cultural tradition."""

    id: str = Field(..., examples=["greek"])
    name: str = Field(..., examples=["Greek Mythology"])
    origin: str = Field(..., examples=["Ancient Greece"])
    description: str
    entity_count: int = Field(0, description="Number of entities in this mythology")


class MythologyDetail(Mythology):
    """Mythology with its entities."""

    entities: list[EntitySummary] = Field(default_factory=list)


class PaginatedResponse(BaseModel):
    """Wrapper for paginated list responses."""

    items: list = Field(default_factory=list)
    total: int = 0
    page: int = 1
    page_size: int = 20
    total_pages: int = 1


class ImageAsset(BaseModel):
    """Reference to a generated image file."""

    entity_id: str
    landscape_url: str = Field("", description="16:9 horizontal image path")
    portrait_url: str = Field("", description="9:16 vertical image path")
