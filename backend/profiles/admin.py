from django.contrib import admin

from .models import Profile


@admin.register(Profile)
class ProfileAdmin(admin.ModelAdmin):
    list_display = ("id", "created_at")
    readonly_fields = ("id", "created_at")
