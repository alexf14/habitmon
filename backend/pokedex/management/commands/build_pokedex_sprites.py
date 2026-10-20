"""Descarga una vez los sprites de Kanto y genera miniaturas y siluetas locales.

Durante el uso normal la aplicación no descarga nada: todo se sirve desde static/.
"""

import io
import urllib.request
from concurrent.futures import ThreadPoolExecutor

from django.conf import settings
from django.core.management.base import BaseCommand
from PIL import Image

SOURCE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/versions/generation-v/black-white"
SILHOUETTE_COLOR = (204, 204, 204, 255)  # gris claro


def _download(url):
    request = urllib.request.Request(url, headers={"User-Agent": "habitmon-prototype"})
    with urllib.request.urlopen(request, timeout=30) as response:
        return response.read()


def make_thumbnail(png_bytes):
    """Recorta el sprite a su contenido y lo centra en un lienzo cuadrado, sin reescalar."""
    image = Image.open(io.BytesIO(png_bytes)).convert("RGBA")
    image = image.crop(image.getchannel("A").getbbox())
    side = max(image.size)
    canvas = Image.new("RGBA", (side, side), (0, 0, 0, 0))
    canvas.paste(image, ((side - image.width) // 2, side - image.height))
    return canvas


def make_silhouette(thumbnail):
    """Sustituye cada píxel visible por gris claro: conserva la forma, oculta los colores."""
    alpha = thumbnail.getchannel("A").point(lambda a: 255 if a > 0 else 0)
    silhouette = Image.new("RGBA", thumbnail.size, SILHOUETTE_COLOR)
    silhouette.putalpha(alpha)
    return silhouette


class Command(BaseCommand):
    help = "Descarga los sprites de las 151 especies y genera miniaturas y siluetas en static/sprites/pokemon/."

    def add_arguments(self, parser):
        parser.add_argument("--force", action="store_true", help="Vuelve a descargar aunque ya existan.")

    def handle(self, *args, force=False, **options):
        root = settings.BASE_DIR / "static" / "sprites" / "pokemon"
        folders = {name: root / name for name in ("animated", "thumbs", "silhouettes")}
        for folder in folders.values():
            folder.mkdir(parents=True, exist_ok=True)

        def build(number):
            n = f"{number:03d}"
            paths = {
                "animated": folders["animated"] / f"{n}.gif",
                "thumbs": folders["thumbs"] / f"{n}.png",
                "silhouettes": folders["silhouettes"] / f"{n}.png",
            }
            if not force and all(p.exists() for p in paths.values()):
                return False
            paths["animated"].write_bytes(_download(f"{SOURCE}/animated/{number}.gif"))
            thumbnail = make_thumbnail(_download(f"{SOURCE}/{number}.png"))
            thumbnail.save(paths["thumbs"], optimize=True)
            make_silhouette(thumbnail).save(paths["silhouettes"], optimize=True)
            return True

        with ThreadPoolExecutor(8) as pool:
            built = sum(pool.map(build, range(1, 152)))
        self.stdout.write(self.style.SUCCESS(f"Sprites generados: {built} · ya existentes: {151 - built}"))
