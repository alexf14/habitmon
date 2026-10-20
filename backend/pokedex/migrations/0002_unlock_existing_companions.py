from django.db import migrations


def unlock_existing_companions(apps, schema_editor):
    """Registra en la Pokédex el compañero de las partidas creadas antes de la iteración 2."""
    Companion = apps.get_model("companions", "Companion")
    PokedexEntry = apps.get_model("pokedex", "PokedexEntry")
    for companion in Companion.objects.all():
        PokedexEntry.objects.get_or_create(
            profile_id=companion.profile_id,
            species_id=companion.species_id,
            defaults={"unlock_reason": "starter"},
        )


class Migration(migrations.Migration):

    dependencies = [
        ("pokedex", "0001_initial"),
        ("companions", "0005_companion_profile"),
    ]

    operations = [
        migrations.RunPython(unlock_existing_companions, migrations.RunPython.noop),
    ]
