from django.urls import path

from . import views

app_name = "companions"

urlpatterns = [
    path("", views.welcome, name="welcome"),
    path("elegir/", views.selection, name="selection"),
    path("presentacion/", views.intro, name="intro"),
    path("companero/", views.home, name="home"),
    path("companions/active", views.set_active, name="set_active"),
]
