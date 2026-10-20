import pytest

from companions import services as companion_services
from companions.models import PokemonSpecies
from pokedex import services
from pokedex.models import PokedexEntry
from profiles.models import Profile

pytestmark = pytest.mark.django_db


@pytest.mark.parametrize("name, number", [("Bulbasaur", 1), ("Charmander", 4), ("Squirtle", 7)])
def test_choosing_starter_unlocks_exactly_that_species(profile, species, name, number):
    companion_services.choose_starter(profile, species(name).pk)

    entries = PokedexEntry.objects.filter(profile=profile)
    assert [e.species.pokedex_number for e in entries] == [number]
    assert entries.get().unlock_reason == services.UnlockReason.STARTER
    assert entries.get().status == services.Status.UNLOCKED


def test_unlock_is_idempotent(profile):
    first, created = services.unlock_pokemon(profile, 25, services.UnlockReason.EVENT)
    again, created_again = services.unlock_pokemon(profile, 25, services.UnlockReason.HABIT)

    assert (created, created_again) == (True, False)
    assert first == again
    assert again.unlock_reason == services.UnlockReason.EVENT
    assert PokedexEntry.objects.filter(profile=profile).count() == 1


def test_evolution_keeps_previous_species(profile, species):
    companion_services.choose_starter(profile, species("Charmander").pk)
    services.unlock_pokemon(profile, 5, services.UnlockReason.EVOLUTION)

    numbers = PokedexEntry.objects.filter(profile=profile).values_list("species__pokedex_number", flat=True)
    assert sorted(numbers) == [4, 5]


def test_unlocks_are_per_profile(profile):
    other = Profile.objects.create()
    services.unlock_pokemon(profile, 150, services.UnlockReason.EVENT)
    assert not PokedexEntry.objects.filter(profile=other).exists()


def test_unknown_species_cannot_be_unlocked(profile):
    with pytest.raises(PokemonSpecies.DoesNotExist):
        services.unlock_pokemon(profile, 152, services.UnlockReason.EVENT)
