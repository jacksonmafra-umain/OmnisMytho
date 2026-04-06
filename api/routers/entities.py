import math
from typing import Annotated

from fastapi import APIRouter, HTTPException, Query

from data.seed_data import ENTITIES
from models import Alignment, Entity, EntityType, PaginatedResponse

router = APIRouter(prefix="/entities", tags=["Entities"])


@router.get(
    "",
    response_model=PaginatedResponse,
    summary="List entities (paginated, filterable)",
    description="Returns entities with optional filters for mythology, type, and alignment. Supports pagination.",
)
async def list_entities(
    mythology_id: Annotated[str | None, Query(description="Filter by mythology ID")] = None,
    type: Annotated[EntityType | None, Query(description="Filter by entity type")] = None,
    alignment: Annotated[Alignment | None, Query(description="Filter by alignment")] = None,
    page: Annotated[int, Query(ge=1, description="Page number")] = 1,
    page_size: Annotated[int, Query(ge=1, le=50, description="Items per page")] = 20,
) -> PaginatedResponse:
    filtered = ENTITIES

    if mythology_id:
        filtered = [e for e in filtered if e.mythology_id == mythology_id]
    if type:
        filtered = [e for e in filtered if e.type == type]
    if alignment:
        filtered = [e for e in filtered if e.alignment == alignment]

    total = len(filtered)
    total_pages = max(1, math.ceil(total / page_size))
    start = (page - 1) * page_size
    items = filtered[start : start + page_size]

    return PaginatedResponse(
        items=items,
        total=total,
        page=page,
        page_size=page_size,
        total_pages=total_pages,
    )


@router.get(
    "/search",
    response_model=list[Entity],
    summary="Search entities by name",
    description="Case-insensitive search across entity names. Returns up to `limit` results.",
)
async def search_entities(
    q: Annotated[str, Query(min_length=1, description="Search query")],
    limit: Annotated[int, Query(ge=1, le=50, description="Max results")] = 10,
) -> list[Entity]:
    query = q.lower()
    return [e for e in ENTITIES if query in e.name.lower()][:limit]


@router.get(
    "/{entity_id}",
    response_model=Entity,
    summary="Get entity details",
    description="Returns full details for a single entity including description, powers, symbols, and image prompt.",
)
async def get_entity(entity_id: str) -> Entity:
    entity = next((e for e in ENTITIES if e.id == entity_id), None)
    if not entity:
        raise HTTPException(status_code=404, detail="Entity not found")
    return entity
