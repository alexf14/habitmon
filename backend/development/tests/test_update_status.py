import pytest
from django.urls import reverse

from companions.models import PokemonSpecies
from pokedex.models import PokedexEntry
from pokedex.selectors import get_pokedex
from profiles.services import get_local_profile

pytestmark = pytest.mark.django_db

SELECTION = reverse("companions:selection")
INTRO = reverse("companions:intro")
POKEDEX_API = "/pokedex"


def put(client, pokemon_id, status):
    return client.put(f"{POKEDEX_API}?id={pokemon_id}&status={status}")


def setup_game(client):
    species = PokemonSpecies.objects.get(name="Charmander")
    client.post(SELECTION, {"species": species.pk})
    client.post(INTRO)


def test_set_status_seen_returns_200(client):
    setup_game(client)
    response = put(client, 1, "SEEN")
    assert response.status_code == 200
    data = response.json()
    assert data["code"] == 200
    assert data["pokemon_id"] == 1
    assert data["status"] == "SEEN"


def test_set_status_unlocked_returns_200(client):
    setup_game(client)
    response = put(client, 1, "UNLOCKED")
    assert response.status_code == 200
    assert response.json()["status"] == "UNLOCKED"


def test_set_status_unknown_returns_200(client):
    setup_game(client)
    put(client, 1, "SEEN")
    response = put(client, 1, "UNKNOWN")
    assert response.status_code == 200
    assert response.json()["status"] == "UNKNOWN"


def test_set_seen_creates_entry(client):
    setup_game(client)
    profile = get_local_profile()
    put(client, 1, "SEEN")
    entry = PokedexEntry.objects.get(profile=profile, species__pokedex_number=1)
    assert entry.status == "SEEN"


def test_set_unlocked_creates_entry(client):
    setup_game(client)
    profile = get_local_profile()
    put(client, 1, "UNLOCKED")
    entry = PokedexEntry.objects.get(profile=profile, species__pokedex_number=1)
    assert entry.status == "UNLOCKED"


def test_set_unknown_removes_entry(client):
    setup_game(client)
    profile = get_local_profile()
    put(client, 1, "SEEN")
    put(client, 1, "UNKNOWN")
    assert not PokedexEntry.objects.filter(profile=profile, species__pokedex_number=1).exists()


def test_regression_unlocked_to_seen_allowed(client):
    setup_game(client)
    profile = get_local_profile()
    put(client, 4, "SEEN")
    entry = PokedexEntry.objects.get(profile=profile, species__pokedex_number=4)
    assert entry.status == "SEEN"


def test_regression_unlocked_to_unknown_allowed(client):
    setup_game(client)
    profile = get_local_profile()
    put(client, 4, "UNKNOWN")
    assert not PokedexEntry.objects.filter(profile=profile, species__pokedex_number=4).exists()


def test_nonexistent_pokemon_returns_404(client):
    setup_game(client)
    response = put(client, 152, "SEEN")
    assert response.status_code == 404
    assert response.json()["code"] == 404


def test_invalid_status_returns_400(client):
    setup_game(client)
    response = put(client, 1, "INVALID")
    assert response.status_code == 400
    assert response.json()["code"] == 400


def test_missing_id_returns_400(client):
    setup_game(client)
    response = client.put(f"{POKEDEX_API}?status=SEEN")
    assert response.status_code == 400
    assert response.json()["code"] == 400


def test_non_integer_id_returns_400(client):
    setup_game(client)
    response = client.put(f"{POKEDEX_API}?id=abc&status=SEEN")
    assert response.status_code == 400
    assert response.json()["code"] == 400


def test_no_duplicate_entries(client):
    setup_game(client)
    profile = get_local_profile()
    put(client, 1, "SEEN")
    put(client, 1, "SEEN")
    assert PokedexEntry.objects.filter(profile=profile, species__pokedex_number=1).count() == 1


def test_first_seen_at_preserved_when_upgrading_to_unlocked(client):
    setup_game(client)
    profile = get_local_profile()
    put(client, 1, "SEEN")
    seen_at = PokedexEntry.objects.get(profile=profile, species__pokedex_number=1).first_seen_at
    put(client, 1, "UNLOCKED")
    entry = PokedexEntry.objects.get(profile=profile, species__pokedex_number=1)
    assert entry.first_seen_at == seen_at


def test_changes_reflected_in_pokedex_list(client):
    setup_game(client)
    profile = get_local_profile()
    put(client, 1, "SEEN")
    items = get_pokedex(profile)
    bulbasaur = next(i for i in items if i.number == 1)
    assert bulbasaur.is_seen


def test_endpoint_unavailable_outside_development(client, settings):
    settings.HABITMON_ALLOW_RESET = False
    setup_game(client)
    response = put(client, 1, "SEEN")
    assert response.status_code == 404
