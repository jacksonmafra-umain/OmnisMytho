"""
Omnis Mytho — API
Encyclopaedia of the Divine & Profane

A REST API serving mythological entities across multiple traditions,
documented with OpenAPI for consumption by the Omnis Mytho mobile app.
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from routers import entities, mythologies

app = FastAPI(
    title="Omnis Mytho API",
    description=(
        "REST API for the Omnis Mytho encyclopaedia — gods, demons, angels, "
        "spirits and creatures from Greek, Norse, Egyptian, Hindu, Japanese "
        "and Christian traditions.\n\n"
        "All images follow a black-and-white grimoire/alchemy illustration style."
    ),
    version="1.0.0",
    contact={"name": "Omnis Mytho", "url": "https://github.com/omnismytho"},
    license_info={"name": "MIT"},
    docs_url="/docs",
    redoc_url="/redoc",
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.mount("/static", StaticFiles(directory="static"), name="static")

app.include_router(mythologies.router, prefix="/api/v1")
app.include_router(entities.router, prefix="/api/v1")


@app.get("/", tags=["Health"])
async def root():
    return {
        "name": "Omnis Mytho API",
        "version": "1.0.0",
        "status": "ab origine mundi",
    }


@app.get("/health", tags=["Health"])
async def health():
    return {"status": "ok"}
