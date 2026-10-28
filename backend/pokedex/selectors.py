"""Lecturas de la Pokédex. Toda la validación de acceso se hace aquí, en el servidor."""

from dataclasses import dataclass

from django.db.models import OuterRef, Subquery

from companions.models import PokemonSpecies

from .models import PokedexEntry

_UNKNOWN = "UNKNOWN"
_SEEN = PokedexEntry.Status.SEEN
_UNLOCKED = PokedexEntry.Status.UNLOCKED


class PokemonUnknown(Exception):
    """La especie no existe o tiene estado UNKNOWN para este perfil."""


@dataclass(frozen=True)
class PokedexItem:
    number: int
    status: str  # "UNKNOWN", "SEEN" o "UNLOCKED"
    name: str
    image_url: str
    is_active_companion: bool = False

    @property
    def number_display(self):
        return f"{self.number:03d}"

    @property
    def is_unknown(self):
        return self.status == _UNKNOWN

    @property
    def is_seen(self):
        return self.status == _SEEN

    @property
    def is_unlocked(self):
        return self.status == _UNLOCKED


def get_pokedex(profile, active_companion_number=None):
    entry_status = Subquery(
        PokedexEntry.objects.filter(profile=profile, species=OuterRef("pk")).values("status")[:1]
    )
    species_qs = PokemonSpecies.objects.annotate(entry_status=entry_status).order_by("pokedex_number")
    items = []
    for s in species_qs:
        status = s.entry_status or _UNKNOWN
        items.append(PokedexItem(
            number=s.pokedex_number,
            status=status,
            name=s.name if status != _UNKNOWN else "???",
            image_url=s.thumbnail_url if status == _UNLOCKED else s.silhouette_url,
            is_active_companion=(s.pokedex_number == active_companion_number),
        ))
    return items


def get_unlocked_count(profile):
    return PokedexEntry.objects.filter(profile=profile, status=_UNLOCKED).count()


def get_total_count():
    return PokemonSpecies.objects.count()


def get_pokemon_detail(profile, species_id):
    """Devuelve (especie, entrada) para SEEN o UNLOCKED; lanza PokemonUnknown si no hay registro."""
    entry = (
        PokedexEntry.objects.select_related("species")
        .filter(profile=profile, species__pokedex_number=species_id)
        .first()
    )
    if entry is None:
        raise PokemonUnknown
    return entry.species, entry
