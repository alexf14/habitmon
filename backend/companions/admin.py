from django.contrib import admin

from .models import Companion, PokemonSpecies


@admin.register(PokemonSpecies)
class PokemonSpeciesAdmin(admin.ModelAdmin):
    list_display = ("pokedex_number", "name", "primary_type", "secondary_type", "selectable")
    list_editable = ("selectable",)
    list_filter = ("selectable", "primary_type")
    search_fields = ("name",)


@admin.register(Companion)
class CompanionAdmin(admin.ModelAdmin):
    list_display = ("display_name", "species", "level", "experience", "created_at")
    readonly_fields = ("id", "created_at", "updated_at")
