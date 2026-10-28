from django.contrib import admin

from .models import ActivityDaily


@admin.register(ActivityDaily)
class ActivityDailyAdmin(admin.ModelAdmin):
    list_display = ("profile", "date", "steps", "kilometers", "updated_at")
    list_filter = ("date",)
    ordering = ("-date",)
