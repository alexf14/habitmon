from django.http import Http404
from django.shortcuts import redirect, render
from django.views.decorators.http import require_GET

from companions.services import get_active_companion
from profiles.services import get_local_profile

from . import selectors


def _active_companion_number(profile):
    companion = get_active_companion(profile)
    return companion.species.pokedex_number if companion else None


def _profile_ready_or_redirect():
    """La Pokédex solo está disponible tras elegir compañero y ver su presentación."""
    profile = get_local_profile()
    companion = get_active_companion(profile)
    if companion is None:
        return profile, redirect("companions:welcome")
    if not companion.intro_completed:
        return profile, redirect("companions:intro")
    return profile, None


@require_GET
def index(request):
    profile, redirection = _profile_ready_or_redirect()
    if redirection:
        return redirection
    return render(
        request,
        "pokedex/index.html",
        {
            "items": selectors.get_pokedex(profile, active_companion_number=_active_companion_number(profile)),
            "unlocked_count": selectors.get_unlocked_count(profile),
            "total_count": selectors.get_total_count(),
        },
    )


@require_GET
def detail(request, number):
    profile, redirection = _profile_ready_or_redirect()
    if redirection:
        return redirection
    try:
        species, entry = selectors.get_pokemon_detail(profile, number)
    except selectors.PokemonUnknown:
        raise Http404
    active_num = _active_companion_number(profile)
    return render(request, "pokedex/detail.html", {
        "species": species,
        "entry": entry,
        "is_active_companion": (species.pokedex_number == active_num),
    })
