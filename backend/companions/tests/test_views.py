import pytest
from django.urls import reverse

from companions.models import Companion, PokemonSpecies

pytestmark = pytest.mark.django_db

WELCOME = reverse("companions:welcome")
SELECTION = reverse("companions:selection")
INTRO = reverse("companions:intro")
HOME = reverse("companions:home")


def pick(client, name):
    return client.post(SELECTION, {"species": PokemonSpecies.objects.get(name=name).pk})


def finish_intro(client):
    return client.post(INTRO)


def test_first_run_shows_welcome(client):
    content = client.get(WELCOME).content.decode()
    assert "¡Bienvenido a HabitMon!" in content
    assert "COMENZAR" in content
    assert SELECTION in content


def test_selection_lists_starters_with_bulbasaur_first(client):
    content = client.get(SELECTION).content.decode()
    assert "ELIGE A TU" in content
    positions = [content.index(f'alt="{name}"') for name in ("Bulbasaur", "Charmander", "Squirtle")]
    assert positions == sorted(positions)
    assert content.index("checked") < positions[1]
    assert "¿QUIERES ELEGIR A {name}?" in content


@pytest.mark.parametrize("url", [INTRO, HOME])
def test_pages_without_companion_go_to_welcome(client, url):
    assert client.get(url).url == WELCOME


def test_choosing_leads_to_intro(client):
    response = pick(client, "Charmander")
    assert response.url == INTRO
    assert Companion.objects.get().species.name == "Charmander"

    content = client.get(INTRO).content.decode()
    for line in ("¡CHARMANDER SE UNE A TI!", "¡TIENES UN NUEVO COMPAÑERO!",
                 "JUNTOS CONSEGUIRÉIS GRANDES COSAS.", "¡QUE COMIENCE VUESTRA AVENTURA!"):
        assert line in content
    assert "CONTINUAR" in content


def test_unfinished_intro_is_resumed(client):
    pick(client, "Squirtle")
    for url in (WELCOME, SELECTION, HOME):
        assert client.get(url).url == INTRO


def test_finishing_intro_opens_home(client):
    pick(client, "Squirtle")
    response = finish_intro(client)
    assert response.url == HOME
    assert Companion.objects.get().intro_completed

    assert client.get(INTRO).url == HOME
    content = client.get(HOME).content.decode()
    assert "SQUIRTLE" in content
    assert "Nv. 1" in content
    for label in ("Felicidad", "Energía", "Vínculo"):
        assert label in content


def test_existing_companion_is_restored_after_restart(client):
    pick(client, "Bulbasaur")
    finish_intro(client)
    client.cookies.clear()  # simula reiniciar la aplicación

    assert client.get(WELCOME).url == HOME
    assert client.get(SELECTION).url == HOME
    assert "BULBASAUR" in client.get(HOME).content.decode()


def test_second_selection_is_ignored(client):
    pick(client, "Bulbasaur")
    pick(client, "Charmander")
    assert Companion.objects.get().species.name == "Bulbasaur"


def test_invalid_species_returns_400(client):
    response = client.post(SELECTION, {"species": "9999"})
    assert response.status_code == 400
    assert not Companion.objects.exists()


def test_home_has_no_reset_button(client):
    pick(client, "Bulbasaur")
    finish_intro(client)
    content = client.get(HOME).content.decode()
    assert "REINICIAR" not in content


# ---------- PUT /companions/active ----------

import json
from pokedex import services as dex_services

SET_ACTIVE = reverse("companions:set_active")


def _unlock(profile, number):
    dex_services.unlock_pokemon(profile, number, dex_services.UnlockReason.EVENT)


def test_set_active_changes_companion(client):
    pick(client, "Charmander")
    finish_intro(client)
    from profiles.services import get_local_profile
    _unlock(get_local_profile(), 25)

    response = client.put(
        SET_ACTIVE,
        data=json.dumps({"pokemon_id": 25}),
        content_type="application/json",
    )
    assert response.status_code == 200
    data = response.json()
    assert data["code"] == 200
    assert data["companion"]["name"] == "Pikachu"
    assert Companion.objects.get().species.name == "Pikachu"


def test_set_active_already_companion_returns_200(client):
    pick(client, "Charmander")
    finish_intro(client)

    response = client.put(
        SET_ACTIVE,
        data=json.dumps({"pokemon_id": 4}),
        content_type="application/json",
    )
    assert response.status_code == 200
    assert "already" in response.json()["message"]


def test_set_active_locked_returns_403(client):
    pick(client, "Charmander")
    finish_intro(client)

    response = client.put(
        SET_ACTIVE,
        data=json.dumps({"pokemon_id": 25}),
        content_type="application/json",
    )
    assert response.status_code == 403


def test_set_active_unknown_pokemon_returns_404(client):
    pick(client, "Charmander")
    finish_intro(client)

    response = client.put(
        SET_ACTIVE,
        data=json.dumps({"pokemon_id": 9999}),
        content_type="application/json",
    )
    assert response.status_code == 404


def test_set_active_invalid_body_returns_400(client):
    pick(client, "Charmander")
    finish_intro(client)

    response = client.put(
        SET_ACTIVE,
        data="not-json",
        content_type="application/json",
    )
    assert response.status_code == 400


def test_set_active_missing_pokemon_id_returns_400(client):
    pick(client, "Charmander")
    finish_intro(client)

    response = client.put(
        SET_ACTIVE,
        data=json.dumps({}),
        content_type="application/json",
    )
    assert response.status_code == 400


def test_set_active_does_not_clear_pokedex(client):
    pick(client, "Charmander")
    finish_intro(client)
    from pokedex.models import PokedexEntry
    from profiles.services import get_local_profile
    _unlock(get_local_profile(), 25)

    client.put(SET_ACTIVE, data=json.dumps({"pokemon_id": 25}), content_type="application/json")
    assert PokedexEntry.objects.count() == 2
