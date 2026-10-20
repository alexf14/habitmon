import pytest

from companions import services
from companions.models import Companion, PokemonSpecies
from pokedex.models import PokedexEntry
from profiles.models import Profile

pytestmark = pytest.mark.django_db

STARTERS = ("Bulbasaur", "Charmander", "Squirtle")


def test_only_the_three_starters_are_selectable():
    assert [s.name for s in services.get_available_starters()] == list(STARTERS)


def test_no_active_companion_on_first_run(profile):
    assert services.get_active_companion(profile) is None


@pytest.mark.parametrize("name", STARTERS)
def test_choose_any_starter_persists_it(profile, species, name):
    companion = services.choose_starter(profile, species(name).pk)

    stored = services.get_active_companion(profile)
    assert stored == companion
    assert stored.species.name == name
    assert (stored.level, stored.experience) == (1, 0)


def test_cannot_choose_a_second_starter(profile, species):
    services.choose_starter(profile, species("Bulbasaur").pk)

    with pytest.raises(services.CompanionAlreadyExists):
        services.choose_starter(profile, species("Squirtle").pk)
    assert Companion.objects.count() == 1


def test_companions_are_isolated_per_profile(profile, species):
    other = Profile.objects.create()
    services.choose_starter(profile, species("Bulbasaur").pk)
    services.choose_starter(other, species("Squirtle").pk)

    assert services.get_active_companion(profile).species.name == "Bulbasaur"
    assert services.get_active_companion(other).species.name == "Squirtle"


def test_non_starter_species_is_rejected(profile, species):
    with pytest.raises(services.InvalidStarter):
        services.choose_starter(profile, species("Pikachu").pk)


def test_disabled_starter_is_rejected(profile, species):
    PokemonSpecies.objects.filter(name="Charmander").update(selectable=False)
    with pytest.raises(services.InvalidStarter):
        services.choose_starter(profile, species("Charmander").pk)


@pytest.mark.parametrize("value", [None, "", "abc", 9999])
def test_garbage_species_id_is_rejected(profile, value):
    with pytest.raises(services.InvalidStarter):
        services.choose_starter(profile, value)


def test_new_companion_starts_with_initial_wellbeing(profile, species):
    companion = services.choose_starter(profile, species("Charmander").pk)
    assert (companion.happiness, companion.energy, companion.bond) == (70, 100, 0)
    assert companion.intro_completed is False


def test_complete_intro_marks_it_done(profile, species):
    companion = services.choose_starter(profile, species("Charmander").pk)
    services.complete_intro(companion)
    companion.refresh_from_db()
    assert companion.intro_completed is True


def test_reset_game_removes_companion_and_collection(profile, species):
    services.choose_starter(profile, species("Squirtle").pk)
    services.reset_game(profile)
    assert services.get_active_companion(profile) is None
    assert not PokedexEntry.objects.filter(profile=profile).exists()
