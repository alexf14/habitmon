import uuid

from django.db import models


class Profile(models.Model):
    """Perfil local que agrupa el compañero activo y la colección de la Pokédex.

    En el prototipo existe un único perfil local; más adelante se asociará a un usuario autenticado.
    """

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        verbose_name = "perfil"
        verbose_name_plural = "perfiles"

    def __str__(self):
        return f"Perfil {self.created_at:%d/%m/%Y}"
