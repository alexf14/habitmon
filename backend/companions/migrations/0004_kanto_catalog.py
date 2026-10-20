import json
from pathlib import Path

from django.db import migrations, models

DATA_FILE = Path(__file__).resolve().parent.parent / "data" / "kanto_species.json"
STARTERS = {1, 4, 7}


def sprite_paths(number):
    n = f"{number:03d}"
    return {
        "sprite_path": f"sprites/pokemon/animated/{n}.gif",
        "thumbnail_path": f"sprites/pokemon/thumbs/{n}.png",
        "silhouette_path": f"sprites/pokemon/silhouettes/{n}.png",
    }


def load_kanto(apps, schema_editor):
    PokemonSpecies = apps.get_model("companions", "PokemonSpecies")
    for row in json.loads(DATA_FILE.read_text(encoding="utf-8")):
        PokemonSpecies.objects.update_or_create(
            pokedex_number=row["number"],
            defaults={
                "name": row["name"],
                "primary_type": row["primary_type"],
                "secondary_type": row["secondary_type"],
                "genus": row["genus"],
                "height_dm": row["height_dm"],
                "weight_hg": row["weight_hg"],
                "description": row["description"],
                "selectable": row["number"] in STARTERS,
                **sprite_paths(row["number"]),
            },
        )


def unload_kanto(apps, schema_editor):
    PokemonSpecies = apps.get_model("companions", "PokemonSpecies")
    PokemonSpecies.objects.exclude(pokedex_number__in=STARTERS).delete()


class Migration(migrations.Migration):

    dependencies = [
        ("companions", "0003_wellbeing_and_intro"),
    ]

    operations = [
        migrations.RenameField("pokemonspecies", "sprite", "sprite_path"),
        migrations.RenameField("pokemonspecies", "enabled", "selectable"),
        migrations.AlterField("pokemonspecies", "selectable", models.BooleanField(default=False)),
        migrations.AddField("pokemonspecies", "thumbnail_path", models.CharField(blank=True, max_length=255)),
        migrations.AddField(
            "pokemonspecies", "silhouette_path", models.CharField(blank=True, max_length=255, null=True)
        ),
        migrations.AddField("pokemonspecies", "genus", models.CharField(blank=True, max_length=60)),
        migrations.AddField("pokemonspecies", "height_dm", models.PositiveSmallIntegerField(default=0)),
        migrations.AddField("pokemonspecies", "weight_hg", models.PositiveIntegerField(default=0)),
        migrations.AddField("pokemonspecies", "description", models.TextField(blank=True)),
        migrations.RunPython(load_kanto, unload_kanto),
    ]
