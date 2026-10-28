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


# ---------- set_active_companion ----------

def _setup_with_unlocked(profile, species, starter="Charmander", extra=25):
    """Crea un compañero inicial y desbloquea otra especie para las pruebas."""
    from pokedex import services as dex_services
    services.choose_starter(profile, species(starter).pk)
    dex_services.unlock_pokemon(profile, extra, dex_services.UnlockReason.EVENT)


def test_set_active_companion_changes_species(profile, species):
    _setup_with_unlocked(profile, species)
    companion = services.set_active_companion(profile, 25)
    assert companion.species.name == "Pikachu"
    assert services.get_active_companion(profile).species.name == "Pikachu"


def test_set_active_companion_is_idempotent(profile, species):
    services.choose_starter(profile, species("Charmander").pk)
    with pytest.raises(services.AlreadyActiveCompanion):
        services.set_active_companion(profile, 4)  # Charmander ya es el compañero


def test_set_active_companion_requires_unlocked(profile, species):
    services.choose_starter(profile, species("Charmander").pk)
    with pytest.raises(services.PokemonNotUnlocked):
        services.set_active_companion(profile, 25)  # Pikachu no está desbloqueado


def test_set_active_companion_requires_unlocked_not_seen(profile, species):
    from pokedex import services as dex_services
    services.choose_starter(profile, species("Charmander").pk)
    dex_services.mark_seen(profile, 25)
    with pytest.raises(services.PokemonNotUnlocked):
        services.set_active_companion(profile, 25)


def test_set_active_companion_unknown_pokemon_raises(profile, species):
    services.choose_starter(profile, species("Charmander").pk)
    with pytest.raises(services.PokemonNotFound):
        services.set_active_companion(profile, 9999)


def test_set_active_companion_does_not_remove_pokedex_entries(profile, species):
    _setup_with_unlocked(profile, species, starter="Charmander", extra=25)
    services.set_active_companion(profile, 25)
    assert PokedexEntry.objects.filter(profile=profile).count() == 2
