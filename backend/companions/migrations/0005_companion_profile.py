import django.db.models.deletion
from django.db import migrations, models


def assign_local_profile(apps, schema_editor):
    """Asigna los compañeros existentes al perfil local para no perder la partida."""
    Companion = apps.get_model("companions", "Companion")
    Profile = apps.get_model("profiles", "Profile")
    for companion in Companion.objects.filter(profile__isnull=True):
        profile = Profile.objects.order_by("created_at").first() or Profile.objects.create()
        if Companion.objects.filter(profile=profile).exists():
            profile = Profile.objects.create()
        companion.profile = profile
        companion.save(update_fields=["profile"])


class Migration(migrations.Migration):

    dependencies = [
        ("companions", "0004_kanto_catalog"),
        ("profiles", "0001_initial"),
    ]

    operations = [
        migrations.AddField(
            "companion",
            "profile",
            models.OneToOneField(
                null=True,
                on_delete=django.db.models.deletion.CASCADE,
                related_name="companion",
                to="profiles.profile",
            ),
        ),
        migrations.RunPython(assign_local_profile, migrations.RunPython.noop),
        migrations.AlterField(
            "companion",
            "profile",
            models.OneToOneField(
                on_delete=django.db.models.deletion.CASCADE,
                related_name="companion",
                to="profiles.profile",
            ),
        ),
    ]
