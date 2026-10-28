from django.conf import settings
from django.contrib import admin
from django.urls import include, path

urlpatterns = [
    path("admin/", admin.site.urls),
    path("pokedex/", include("pokedex.urls")),
    path("activity/", include("activity.urls")),
    path("", include("companions.urls")),
]

if settings.HABITMON_ALLOW_RESET:
    urlpatterns += [path("", include("development.urls"))]
