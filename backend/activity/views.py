import json

from django.http import JsonResponse
from django.shortcuts import redirect, render
from django.views.decorators.csrf import csrf_exempt
from django.views.decorators.http import require_GET, require_http_methods

from companions.services import get_active_companion
from profiles.services import get_local_profile

from . import selectors, services


def _profile_ready_or_redirect():
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
    entry = services.get_daily_activity(profile)
    return render(request, "activity/index.html", selectors.build_progress(entry))


@csrf_exempt
@require_http_methods(["PUT"])
def update_daily(request):
    try:
        body = json.loads(request.body)
    except (json.JSONDecodeError, UnicodeDecodeError):
        return JsonResponse({"code": 400, "message": "Invalid request body"}, status=400)

    steps = body.get("steps")
    kilometers = body.get("kilometers")

    if steps is None and kilometers is None:
        return JsonResponse({"code": 400, "message": "At least one field must be provided"}, status=400)

    if (steps is not None and steps < 0) or (kilometers is not None and kilometers < 0):
        return JsonResponse({"code": 400, "message": "Steps and kilometers must be non-negative"}, status=400)

    try:
        profile = get_local_profile()
        entry = services.update_daily_activity(profile, steps=steps, kilometers=kilometers)
        data = selectors.build_progress(entry)
    except Exception:
        return JsonResponse({"code": 500, "message": "Unable to update daily activity"}, status=500)

    return JsonResponse({"code": 200, "message": "Daily activity updated successfully", "data": data})


@csrf_exempt
@require_http_methods(["POST"])
def increment_steps_view(request):
    try:
        body = json.loads(request.body) if request.body else {}
        count = int(body.get("count", 1))
    except (json.JSONDecodeError, ValueError, TypeError):
        return JsonResponse({"code": 400, "message": "Invalid request body"}, status=400)

    if count < 0:
        return JsonResponse({"code": 400, "message": "Count must be non-negative"}, status=400)

    try:
        profile = get_local_profile()
        entry = services.increment_steps(profile, count=count)
        data = selectors.build_progress(entry)
    except Exception:
        return JsonResponse({"code": 500, "message": "Unable to increment steps"}, status=500)

    return JsonResponse({"code": 200, "message": "Steps incremented successfully", "data": data})
