from companions.models import PokemonSpecies
from companions.services import reset_game
from pokedex import services as pokedex_services
from profiles.services import get_local_profile


class PokemonNotFound(Exception):
    pass


class ProfileNotFound(Exception):
    pass


def restart():
    """Reinicia la partida del perfil local. Idempotente."""
    profile = get_local_profile()
    reset_game(profile)


def update_pokemon_status(pokemon_id, status):
    """Actualiza el estado de una especie en la Pokédex del perfil local."""
    if not PokemonSpecies.objects.filter(pokedex_number=pokemon_id).exists():
        raise PokemonNotFound(f"Pokemon #{pokemon_id} not found")
    profile = get_local_profile()
    pokedex_services.set_pokemon_status(profile, pokemon_id, status)
