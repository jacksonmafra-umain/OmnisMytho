from fastapi import APIRouter, HTTPException

from data.loader import ENTITIES, MYTHOLOGIES
from models import EntitySummary, Mythology, MythologyDetail

router = APIRouter(prefix="/mythologies", tags=["Mythologies"])


@router.get(
    "",
    response_model=list[Mythology],
    summary="List all mythologies",
    description="Returns every available mythology with basic metadata and entity count.",
)
async def list_mythologies() -> list[Mythology]:
    return MYTHOLOGIES


@router.get(
    "/{mythology_id}",
    response_model=MythologyDetail,
    summary="Get mythology details",
    description="Returns a single mythology with its full list of entity summaries.",
)
async def get_mythology(mythology_id: str) -> MythologyDetail:
    mythology = next((m for m in MYTHOLOGIES if m.id == mythology_id), None)
    if not mythology:
        raise HTTPException(status_code=404, detail="Mythology not found")

    entities = [
        EntitySummary(
            id=e.id,
            name=e.name,
            type=e.type,
            title=e.title,
            alignment=e.alignment,
            mythology_id=e.mythology_id,
        )
        for e in ENTITIES
        if e.mythology_id == mythology_id
    ]

    return MythologyDetail(**mythology.model_dump(), entities=entities)
