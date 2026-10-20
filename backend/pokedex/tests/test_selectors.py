import pytest
from django.conf import settings

from companions.models import PokemonSpecies
from pokedex import selectors, services

pytestmark = pytest.mark.django_db


def test_pokedex_has_151_species_in_order(profile):
    items = selectors.get_pokedex(profile)
    assert len(items) == 151
    assert [i.number for i in items] == list(range(1, 152))
    assert items[-1].number_display == "151"


def test_locked_species_hide_name_and_color_sprite(profile):
    services.unlock_pokemon(profile, 4, services.UnlockReason.STARTER)
    items = {i.number: i for i in selectors.get_pokedex(profile)}

    charmander, bulbasaur = items[4], items[1]
    assert charmander.is_unlocked and charmander.name == "Charmander"
    assert "thumbs/004.png" in charmander.image_url
    assert bulbasaur.is_unknown and bulbasaur.name == "???"
    assert "silhouettes/001.png" in bulbasaur.image_url


def test_unlocked_count(profile):
    assert selectors.get_unlocked_count(profile) == 0
    services.unlock_pokemon(profile, 4, services.UnlockReason.STARTER)
    services.unlock_pokemon(profile, 5, services.UnlockReason.EVOLUTION)
    assert selectors.get_unlocked_count(profile) == 2
    assert selectors.get_total_count() == 151


def test_detail_only_for_unlocked_species(profile):
    services.unlock_pokemon(profile, 4, services.UnlockReason.STARTER)

    species, entry = selectors.get_pokemon_detail(profile, 4)
    assert species.name == "Charmander"
    assert entry.unlock_reason == services.UnlockReason.STARTER

    for number in (1, 151, 999):
        with pytest.raises(selectors.PokemonUnknown):
            selectors.get_pokemon_detail(profile, number)


def test_every_species_has_local_assets():
    static_root = settings.BASE_DIR / "static"
    for s in PokemonSpecies.objects.all():
        for path in (s.sprite_path, s.thumbnail_path, s.silhouette_path):
            assert (static_root / path).is_file(), path
