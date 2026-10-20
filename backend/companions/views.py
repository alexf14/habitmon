from django.http import HttpResponseBadRequest
from django.shortcuts import redirect, render
from django.views.decorators.http import require_http_methods

from profiles.services import get_local_profile

from . import services, texts


def _resume(companion):
    """Pantalla en la que debe continuar un compañero ya existente."""
    if companion.intro_completed:
        return redirect("companions:home")
    return redirect("companions:intro")


def welcome(request):
    companion = services.get_active_companion(get_local_profile())
    if companion:
        return _resume(companion)
    return render(request, "companions/welcome.html")


@require_http_methods(["GET", "POST"])
def selection(request):
    profile = get_local_profile()
    companion = services.get_active_companion(profile)
    if companion:
        return _resume(companion)

    if request.method == "POST":
        try:
            services.choose_starter(profile, request.POST.get("species"))
        except services.CompanionAlreadyExists:
            return _resume(services.get_active_companion(profile))
        except services.InvalidStarter:
            return HttpResponseBadRequest("Pokémon no válido.")
        return redirect("companions:intro")

    return render(
        request,
        "companions/selection.html",
        {"starters": services.get_available_starters(), "confirm_text": texts.CONFIRM_CHOICE},
    )


@require_http_methods(["GET", "POST"])
def intro(request):
    companion = services.get_active_companion(get_local_profile())
    if companion is None:
        return redirect("companions:welcome")
    if request.method == "POST":
        services.complete_intro(companion)
        return redirect("companions:home")
    if companion.intro_completed:
        return redirect("companions:home")
    return render(
        request,
        "companions/intro.html",
        {"companion": companion, "lines": texts.intro_lines(companion.species.name)},
    )


def home(request):
    companion = services.get_active_companion(get_local_profile())
    if companion is None:
        return redirect("companions:welcome")
    if not companion.intro_completed:
        return redirect("companions:intro")
    return render(
        request,
        "companions/home.html",
        {
            "companion": companion,
            "stats": [
                ("FEL", "Felicidad", companion.happiness),
                ("ENE", "Energía", companion.energy),
                ("VIN", "Vínculo", companion.bond),
            ],
        },
    )
