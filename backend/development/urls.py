from django.urls import path

from . import views

app_name = "development"

urlpatterns = [
    path("restart", views.restart, name="restart"),
    path("pokedex", views.update_status, name="update_status"),
]
