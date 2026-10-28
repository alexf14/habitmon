import json

from django.http import HttpResponseBadRequest, JsonResponse
from django.shortcuts import redirect, render
from django.views.decorators.csrf import csrf_exempt
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


@csrf_exempt
@require_http_methods(["PUT"])
def set_active(request):
    try:
        body = json.loads(request.body)
    except (json.JSONDecodeError, UnicodeDecodeError):
        return JsonResponse({"code": 400, "message": "Invalid request body"}, status=400)

    try:
        pokemon_id = int(body.get("pokemon_id"))
    except (ValueError, TypeError):
        return JsonResponse({"code": 400, "message": "Invalid pokemon_id"}, status=400)

    profile = get_local_profile()

    try:
        companion = services.set_active_companion(profile, pokemon_id)
    except services.AlreadyActiveCompanion:
        return JsonResponse({"code": 200, "message": "Pokemon is already the active companion"})
    except services.PokemonNotFound:
        return JsonResponse({"code": 404, "message": "Pokemon not found"}, status=404)
    except services.PokemonNotUnlocked:
        return JsonResponse({"code": 403, "message": "Pokemon must be unlocked to become a companion"}, status=403)
    except Exception:
        return JsonResponse({"code": 500, "message": "Unable to update companion"}, status=500)

    return JsonResponse({
        "code": 200,
        "message": "Active companion updated successfully",
        "companion": {
            "pokemon_id": companion.species.pokedex_number,
            "name": companion.species.name,
        },
    })


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
