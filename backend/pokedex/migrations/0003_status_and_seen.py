from django.db import migrations, models


def populate_status_and_dates(apps, schema_editor):
    PokedexEntry = apps.get_model("pokedex", "PokedexEntry")
    for entry in PokedexEntry.objects.all():
        entry.status = "UNLOCKED"
        entry.first_seen_at = entry.unlocked_at
        entry.save()


class Migration(migrations.Migration):

    dependencies = [
        ("pokedex", "0002_unlock_existing_companions"),
    ]

    operations = [
        migrations.AddField(
            model_name="pokedexentry",
            name="status",
            field=models.CharField(
                choices=[("SEEN", "Visto"), ("UNLOCKED", "Desbloqueado")],
                default="UNLOCKED",
                max_length=10,
            ),
        ),
        migrations.AddField(
            model_name="pokedexentry",
            name="first_seen_at",
            field=models.DateTimeField(blank=True, null=True),
        ),
        migrations.RunPython(populate_status_and_dates, migrations.RunPython.noop),
        migrations.AlterField(
            model_name="pokedexentry",
            name="unlocked_at",
            field=models.DateTimeField(blank=True, null=True),
        ),
        migrations.AlterField(
            model_name="pokedexentry",
            name="unlock_reason",
            field=models.CharField(
                blank=True,
                choices=[
                    ("starter", "Compañero inicial"),
                    ("evolution", "Evolución"),
                    ("goal", "Objetivo cumplido"),
                    ("habit", "Recompensa por hábitos"),
                    ("event", "Evento o logro"),
                    ("debug", "Depuración"),
                ],
                max_length=20,
            ),
        ),
    ]
