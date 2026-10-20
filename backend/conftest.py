import pytest

from companions.models import PokemonSpecies
from profiles.services import get_local_profile


@pytest.fixture
def profile(db):
    return get_local_profile()


@pytest.fixture
def species():
    def by_name(name):
        return PokemonSpecies.objects.get(name=name)

    return by_name
