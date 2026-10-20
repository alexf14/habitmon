"""Textos narrativos, separados de las plantillas para poder editarlos y traducirlos."""

from django.utils.translation import gettext_lazy as _

CONFIRM_CHOICE = _("¿QUIERES ELEGIR A {name}?")

INTRO_LINES = (
    _("¡{name} SE UNE A TI!"),
    _("¡TIENES UN NUEVO COMPAÑERO!"),
    _("JUNTOS CONSEGUIRÉIS GRANDES COSAS."),
    _("¡QUE COMIENCE VUESTRA AVENTURA!"),
)

CONFIRM_RESET = _("¿BORRAR LA PARTIDA Y EMPEZAR DE CERO?")


def intro_lines(name):
    return [str(line).format(name=name.upper()) for line in INTRO_LINES]
