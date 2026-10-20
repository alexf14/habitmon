import pytest
from django.urls import reverse

from companions.models import Companion, PokemonSpecies
from pokedex.models import PokedexEntry

pytestmark = pytest.mark.django_db

RESTART = "/restart"
SELECTION = reverse("companions:selection")
INTRO = reverse("companions:intro")
HOME = reverse("companions:home")
WELCOME = reverse("companions:welcome")


def setup_game(client):
    species = PokemonSpecies.objects.get(name="Charmander")
    client.post(SELECTION, {"species": species.pk})
    client.post(INTRO)


def test_restart_returns_200(client):
    setup_game(client)
    response = client.delete(RESTART)
    assert response.status_code == 200
    data = response.json()
    assert data["code"] == 200
    assert "restarted" in data["message"].lower()


def test_restart_removes_active_companion(client):
    setup_game(client)
    assert Companion.objects.exists()
    client.delete(RESTART)
    assert not Companion.objects.exists()


def test_restart_clears_pokedex_collection(client):
    setup_game(client)
    assert PokedexEntry.objects.exists()
    client.delete(RESTART)
    assert not PokedexEntry.objects.exists()


def test_restart_welcome_flow_resumes(client):
    setup_game(client)
    client.delete(RESTART)
    response = client.get(HOME)
    assert response.url == WELCOME


def test_restart_preserves_species_catalog(client):
    setup_game(client)
    count_before = PokemonSpecies.objects.count()
    client.delete(RESTART)
    assert PokemonSpecies.objects.count() == count_before


def test_restart_is_idempotent(client):
    setup_game(client)
    r1 = client.delete(RESTART)
    r2 = client.delete(RESTART)
    assert r1.status_code == 200
    assert r2.status_code == 200
    assert not Companion.objects.exists()


def test_restart_does_not_affect_other_profiles(client):
    from profiles.models import Profile
    from companions.services import reset_game

    setup_game(client)
    other_profile = Profile.objects.create()
    species = PokemonSpecies.objects.get(name="Bulbasaur")
    Companion.objects.create(profile=other_profile, species=species)

    client.delete(RESTART)

    assert Companion.objects.filter(profile=other_profile).exists()


def test_restart_unavailable_when_not_in_development(client, settings):
    settings.HABITMON_ALLOW_RESET = False
    setup_game(client)
    response = client.delete(RESTART)
    assert response.status_code == 404
    assert Companion.objects.exists()
