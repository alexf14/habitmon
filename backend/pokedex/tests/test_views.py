import pytest
from django.urls import reverse

from pokedex import services

pytestmark = pytest.mark.django_db

INDEX = reverse("pokedex:index")
HOME = reverse("companions:home")


def start_game(client, species, name="Charmander"):
    client.post(reverse("companions:selection"), {"species": species(name).pk})
    client.post(reverse("companions:intro"))


def test_pokedex_requires_a_companion(client):
    assert client.get(INDEX).url == reverse("companions:welcome")


def test_pokedex_requires_finished_intro(client, species):
    client.post(reverse("companions:selection"), {"species": species("Squirtle").pk})
    assert client.get(INDEX).url == reverse("companions:intro")


def test_pokedex_lists_all_species_with_counter(client, species):
    start_game(client, species)
    html = client.get(INDEX).content.decode()

    assert "POKÉDEX" in html
    assert "<strong>001</strong>/151" in html
    assert html.count('class="dex-row') == 151
    assert "#151" in html
    assert html.count("???") == 150
    assert "CHARMANDER" in html
    assert reverse("pokedex:detail", args=[4]) in html


def test_seen_species_shows_name_and_silhouette_in_list(client, species, profile):
    start_game(client, species)
    services.mark_seen(profile, 1)
    html = client.get(INDEX).content.decode()

    assert "BULBASAUR" in html
    assert "VISTO" in html
    assert reverse("pokedex:detail", args=[1]) in html


def test_seen_species_detail_shows_reduced_info(client, species, profile):
    start_game(client, species)
    services.mark_seen(profile, 1)
    html = client.get(reverse("pokedex:detail", args=[1])).content.decode()

    assert "BULBASAUR" in html
    assert "#001" in html
    assert "Planta" in html
    assert "VISTO" in html


def test_unknown_species_do_not_leak_details(client, species):
    start_game(client, species)
    html = client.get(INDEX).content.decode()

    for hidden in ("Bulbasaur", "BULBASAUR", "Mew", "MEW", "thumbs/001.png", "animated/001.gif"):
        assert hidden not in html
    assert reverse("pokedex:detail", args=[1]) not in html


def test_detail_of_unlocked_species(client, species):
    start_game(client, species)
    html = client.get(reverse("pokedex:detail", args=[4])).content.decode()

    assert "CHARMANDER" in html
    assert "#004" in html
    assert "Fuego" in html
    assert "Pokémon Lagartija" in html
    assert "DESBLOQUEADO" in html


@pytest.mark.parametrize("number", [1, 7, 152, 999])
def test_detail_of_unknown_species_is_404(client, species, number):
    start_game(client, species)
    assert client.get(reverse("pokedex:detail", args=[number])).status_code == 404


def test_counter_reflects_new_unlocks_and_persists(client, species, profile):
    start_game(client, species)
    services.unlock_pokemon(profile, 5, services.UnlockReason.EVOLUTION)
    client.cookies.clear()  # simula reiniciar la aplicación

    html = client.get(INDEX).content.decode()
    assert "<strong>002</strong>/151" in html
    assert "CHARMELEON" in html


def test_swipe_targets_are_declared(client, species):
    start_game(client, species)
    home = client.get(HOME).content.decode()
    dex = client.get(INDEX).content.decode()
    assert f'data-swipe-left="{INDEX}"' in home
    assert f'data-swipe-right="{HOME}"' in dex


def test_no_public_unlock_endpoint(client, species):
    start_game(client, species)
    assert client.post(INDEX, {"species": 150}).status_code == 405
