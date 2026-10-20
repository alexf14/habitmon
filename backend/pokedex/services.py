"""Escrituras de la Pokédex. Solo las llaman otras mecánicas del servidor."""

from django.db import IntegrityError, transaction
from django.utils import timezone

from companions.models import PokemonSpecies

from .models import PokedexEntry

UnlockReason = PokedexEntry.UnlockReason
Status = PokedexEntry.Status


def unlock_pokemon(profile, species_id, reason):
    """Desbloquea una especie (por número de Pokédex). Idempotente: devuelve (entrada, creada)."""
    species = PokemonSpecies.objects.get(pokedex_number=species_id)
    now = timezone.now()
    try:
        with transaction.atomic():
            entry, created = PokedexEntry.objects.get_or_create(
                profile=profile,
                species=species,
                defaults={
                    "status": Status.UNLOCKED,
                    "first_seen_at": now,
                    "unlocked_at": now,
                    "unlock_reason": reason,
                },
            )
            if not created and entry.status != Status.UNLOCKED:
                entry.status = Status.UNLOCKED
                entry.unlocked_at = now
                entry.save(update_fields=["status", "unlocked_at"])
            return entry, created
    except IntegrityError:
        return PokedexEntry.objects.get(profile=profile, species=species), False


def mark_seen(profile, species_id):
    """Marca una especie como vista. No sobreescribe UNLOCKED."""
    species = PokemonSpecies.objects.get(pokedex_number=species_id)
    now = timezone.now()
    entry, created = PokedexEntry.objects.get_or_create(
        profile=profile,
        species=species,
        defaults={"status": Status.SEEN, "first_seen_at": now},
    )
    return entry, created


def set_pokemon_status(profile, species_id, status):
    """Solo para depuración: establece cualquier estado, incluyendo regresiones."""
    species = PokemonSpecies.objects.get(pokedex_number=species_id)
    now = timezone.now()
    with transaction.atomic():
        if status == "UNKNOWN":
            PokedexEntry.objects.filter(profile=profile, species=species).delete()
        elif status == "SEEN":
            entry, created = PokedexEntry.objects.get_or_create(
                profile=profile,
                species=species,
                defaults={"status": Status.SEEN, "first_seen_at": now},
            )
            if not created:
                entry.status = Status.SEEN
                entry.unlocked_at = None
                if not entry.first_seen_at:
                    entry.first_seen_at = now
                entry.save(update_fields=["status", "unlocked_at", "first_seen_at"])
        elif status == "UNLOCKED":
            entry, created = PokedexEntry.objects.get_or_create(
                profile=profile,
                species=species,
                defaults={
                    "status": Status.UNLOCKED,
                    "first_seen_at": now,
                    "unlocked_at": now,
                    "unlock_reason": UnlockReason.DEBUG,
                },
            )
            if not created and entry.status != Status.UNLOCKED:
                entry.status = Status.UNLOCKED
                if not entry.first_seen_at:
                    entry.first_seen_at = now
                entry.unlocked_at = now
                entry.unlock_reason = UnlockReason.DEBUG
                entry.save(update_fields=["status", "first_seen_at", "unlocked_at", "unlock_reason"])


def clear_pokedex(profile):
    PokedexEntry.objects.filter(profile=profile).delete()
