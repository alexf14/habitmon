from django.urls import path

from . import views

app_name = "activity"

urlpatterns = [
    path("", views.index, name="index"),
    path("daily", views.update_daily, name="update_daily"),
    path("daily/steps/increment", views.increment_steps_view, name="increment_steps"),
]
