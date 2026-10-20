import uuid

from django.core.validators import MaxValueValidator
from django.db import models
from django.templatetags.static import static


def _static_or_url(path):
    if path.startswith(("http://", "https://")):
        return path
    return static(path)


class PokemonSpecies(models.Model):
    """Catálogo de especies de Kanto. El número de Pokédex es su identificador natural."""

    pokedex_number = models.PositiveSmallIntegerField(unique=True)
    name = models.CharField(max_length=50, unique=True)
    primary_type = models.CharField(max_length=20)
    secondary_type = models.CharField(max_length=20, blank=True, null=True)
    genus = models.CharField(max_length=60, blank=True)
    height_dm = models.PositiveSmallIntegerField(default=0)
    weight_hg = models.PositiveIntegerField(default=0)
    description = models.TextField(blank=True)
    # Rutas relativas dentro de static/ (o URL absoluta).
    sprite_path = models.CharField(max_length=255)
    thumbnail_path = models.CharField(max_length=255, blank=True)
    silhouette_path = models.CharField(max_length=255, blank=True, null=True)
    selectable = models.BooleanField(default=False)

    class Meta:
        ordering = ["pokedex_number"]
        verbose_name = "especie"
        verbose_name_plural = "especies"

    def __str__(self):
        return f"#{self.number_display} {self.name}"

    @property
    def number_display(self):
        return f"{self.pokedex_number:03d}"

    @property
    def sprite_url(self):
        return _static_or_url(self.sprite_path)

    @property
    def thumbnail_url(self):
        return _static_or_url(self.thumbnail_path or self.sprite_path)

    @property
    def silhouette_url(self):
        return _static_or_url(self.silhouette_path) if self.silhouette_path else ""

    @property
    def types_display(self):
        return " / ".join(t for t in (self.primary_type, self.secondary_type) if t)

    @property
    def height_m(self):
        return self.height_dm / 10

    @property
    def weight_kg(self):
        return self.weight_hg / 10


def wellbeing_field(default):
    return models.PositiveSmallIntegerField(default=default, validators=[MaxValueValidator(100)])


class Companion(models.Model):
    """Compañero activo de un perfil."""

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    profile = models.OneToOneField("profiles.Profile", on_delete=models.CASCADE, related_name="companion")
    species = models.ForeignKey(PokemonSpecies, on_delete=models.PROTECT, related_name="companions")
    nickname = models.CharField(max_length=30, blank=True, null=True)
    level = models.PositiveIntegerField(default=1)
    experience = models.PositiveIntegerField(default=0)
    # Bienestar (0-100). Nunca provoca que el compañero muera o desaparezca.
    happiness = wellbeing_field(70)
    energy = wellbeing_field(100)
    bond = wellbeing_field(0)
    intro_completed = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    class Meta:
        verbose_name = "compañero"
        verbose_name_plural = "compañeros"

    def __str__(self):
        return self.display_name

    @property
    def display_name(self):
        return self.nickname or self.species.name
