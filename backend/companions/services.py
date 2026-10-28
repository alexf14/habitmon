"""Reglas de negocio de la selección del compañero (RF-001 a RF-007)."""

from django.db import transaction

from pokedex import services as pokedex_services

from .models import Companion, PokemonSpecies


class CompanionAlreadyExists(Exception):
    """Ya existe un compañero activo (RF-007)."""


class InvalidStarter(Exception):
    """La especie no es un inicial seleccionable (RF-002)."""


class PokemonNotFound(Exception):
    """La especie no existe en el catálogo."""


class PokemonNotUnlocked(Exception):
    """La especie no está desbloqueada en la Pokédex del perfil."""


class AlreadyActiveCompanion(Exception):
    """La especie indicada ya es el compañero activo."""


def get_active_companion(profile):
    """Devuelve el compañero del perfil, o None (RF-001, RF-006)."""
    return Companion.objects.select_related("species").filter(profile=profile).first()


def get_available_starters():
    return PokemonSpecies.objects.filter(selectable=True)


@transaction.atomic
def choose_starter(profile, species_id):
    """Guarda el inicial elegido (RF-005) y lo registra en la Pokédex del perfil."""
    if Companion.objects.select_for_update().filter(profile=profile).exists():
        raise CompanionAlreadyExists
    try:
        species = get_available_starters().get(pk=species_id)
    except (PokemonSpecies.DoesNotExist, ValueError, TypeError):
        raise InvalidStarter
    companion = Companion.objects.create(profile=profile, species=species)
    pokedex_services.unlock_pokemon(profile, species.pokedex_number, pokedex_services.UnlockReason.STARTER)
    return companion


def complete_intro(companion):
    if not companion.intro_completed:
        companion.intro_completed = True
        companion.save(update_fields=["intro_completed", "updated_at"])


@transaction.atomic
def set_active_companion(profile, pokemon_id):
    """Cambia la especie del compañero activo. El Pokémon debe estar UNLOCKED."""
    from pokedex.models import PokedexEntry

    try:
        species = PokemonSpecies.objects.get(pokedex_number=pokemon_id)
    except PokemonSpecies.DoesNotExist:
        raise PokemonNotFound

    companion = get_active_companion(profile)
    if companion is None:
        raise PokemonNotFound

    if companion.species.pokedex_number == pokemon_id:
        raise AlreadyActiveCompanion

    try:
        entry = PokedexEntry.objects.get(profile=profile, species=species)
    except PokedexEntry.DoesNotExist:
        raise PokemonNotUnlocked

    if entry.status != PokedexEntry.Status.UNLOCKED:
        raise PokemonNotUnlocked

    companion.species = species
    companion.save(update_fields=["species", "updated_at"])
    return companion


@transaction.atomic
def reset_game(profile):
    """Solo para pruebas: borra el compañero y la colección del perfil."""
    Companion.objects.filter(profile=profile).delete()
    pokedex_services.clear_pokedex(profile)
