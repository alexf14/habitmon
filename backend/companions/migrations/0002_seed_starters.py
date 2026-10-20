from django.db import migrations

# Sprites animados del repositorio PokeAPI/sprites (enlazados, no redistribuidos).
# Los derechos de los diseños pertenecen a Nintendo / Game Freak / The Pokémon Company;
# ver la sección 10.7 del documento de diseño antes de cualquier uso público.
SPRITE_URL = (
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"
    "versions/generation-v/black-white/animated/{}.gif"
)

STARTERS = [
    (1, "Bulbasaur", "Planta", "Veneno"),
    (4, "Charmander", "Fuego", None),
    (7, "Squirtle", "Agua", None),
]


def seed(apps, schema_editor):
    PokemonSpecies = apps.get_model("companions", "PokemonSpecies")
    for number, name, primary, secondary in STARTERS:
        PokemonSpecies.objects.update_or_create(
            pokedex_number=number,
            defaults={
                "name": name,
                "primary_type": primary,
                "secondary_type": secondary,
                "sprite": SPRITE_URL.format(number),
                "enabled": True,
            },
        )


def unseed(apps, schema_editor):
    PokemonSpecies = apps.get_model("companions", "PokemonSpecies")
    PokemonSpecies.objects.filter(pokedex_number__in=[s[0] for s in STARTERS]).delete()


class Migration(migrations.Migration):

    dependencies = [
        ("companions", "0001_initial"),
    ]

    operations = [
        migrations.RunPython(seed, unseed),
    ]
