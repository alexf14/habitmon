from django.contrib import admin

from .models import PokedexEntry


@admin.register(PokedexEntry)
class PokedexEntryAdmin(admin.ModelAdmin):
    list_display = ("species", "profile", "unlock_reason", "unlocked_at")
    list_filter = ("unlock_reason",)
    readonly_fields = ("id", "unlocked_at")
