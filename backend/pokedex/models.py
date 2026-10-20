import uuid

from django.db import models


class PokedexEntry(models.Model):
    """Entrada de Pokédex para un perfil. La ausencia de registro equivale a UNKNOWN."""

    class Status(models.TextChoices):
        SEEN = "SEEN", "Visto"
        UNLOCKED = "UNLOCKED", "Desbloqueado"

    class UnlockReason(models.TextChoices):
        STARTER = "starter", "Compañero inicial"
        EVOLUTION = "evolution", "Evolución"
        GOAL = "goal", "Objetivo cumplido"
        HABIT = "habit", "Recompensa por hábitos"
        EVENT = "event", "Evento o logro"
        DEBUG = "debug", "Depuración"

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    profile = models.ForeignKey("profiles.Profile", on_delete=models.CASCADE, related_name="pokedex_entries")
    species = models.ForeignKey("companions.PokemonSpecies", on_delete=models.CASCADE, related_name="pokedex_entries")
    status = models.CharField(max_length=10, choices=Status.choices, default=Status.UNLOCKED)
    first_seen_at = models.DateTimeField(null=True, blank=True)
    unlocked_at = models.DateTimeField(null=True, blank=True)
    unlock_reason = models.CharField(max_length=20, choices=UnlockReason.choices, blank=True)

    class Meta:
        verbose_name = "entrada de Pokédex"
        verbose_name_plural = "entradas de Pokédex"
        constraints = [
            models.UniqueConstraint(fields=["profile", "species"], name="unique_pokedex_entry"),
        ]

    def __str__(self):
        return f"{self.species} ({self.status})"
