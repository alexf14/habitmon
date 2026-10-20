from .models import Profile


def get_local_profile():
    """Devuelve el perfil local del prototipo, creándolo la primera vez."""
    profile = Profile.objects.order_by("created_at").first()
    return profile or Profile.objects.create()
