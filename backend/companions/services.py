"""Reglas de negocio de la selección del compañero (RF-001 a RF-007)."""

from django.db import transaction

from pokedex import services as pokedex_services

from .models import Companion, PokemonSpecies


class CompanionAlreadyExists(Exception):
    """Ya existe un compañero activo (RF-007)."""


class InvalidStarter(Exception):
    """La especie no es un inicial seleccionable (RF-002)."""


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
def reset_game(profile):
    """Solo para pruebas: borra el compañero y la colección del perfil."""
    Companion.objects.filter(profile=profile).delete()
    pokedex_services.clear_pokedex(profile)
