from django.conf import settings
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_http_methods

from . import services

_VALID_STATUSES = {"UNKNOWN", "SEEN", "UNLOCKED"}


def _dev_only(view):
    def wrapper(request, *args, **kwargs):
        if not settings.HABITMON_ALLOW_RESET:
            return JsonResponse({"code": 404, "message": "Not found"}, status=404)
        return view(request, *args, **kwargs)
    return wrapper


@csrf_exempt
@_dev_only
@require_http_methods(["DELETE"])
def restart(request):
    try:
        services.restart()
        return JsonResponse({"code": 200, "message": "Application restarted successfully"})
    except Exception:
        return JsonResponse({"code": 500, "message": "Unable to restart application"}, status=500)


@csrf_exempt
@_dev_only
@require_http_methods(["PUT"])
def update_status(request):
    raw_id = request.GET.get("id", "")
    status = request.GET.get("status", "").upper()

    try:
        pokemon_id = int(raw_id)
    except (ValueError, TypeError):
        return JsonResponse({"code": 400, "message": "Invalid Pokemon id"}, status=400)

    if status not in _VALID_STATUSES:
        return JsonResponse({"code": 400, "message": "Invalid Pokemon status"}, status=400)

    try:
        services.update_pokemon_status(pokemon_id, status)
    except services.PokemonNotFound:
        return JsonResponse({"code": 404, "message": "Pokemon not found"}, status=404)
    except services.ProfileNotFound:
        return JsonResponse({"code": 404, "message": "Test profile not found"}, status=404)
    except Exception:
        return JsonResponse({"code": 500, "message": "Unable to update Pokemon status"}, status=500)

    return JsonResponse({
        "code": 200,
        "message": "Pokemon status updated successfully",
        "pokemon_id": pokemon_id,
        "status": status,
    })
