# HabitMon

HabitMon es una aplicación de hábitos con una mascota virtual inspirada en Tamagotchi y en los
Pokémon clásicos de Game Boy. Eliges un Pokémon inicial y, en próximas versiones, este crecerá,
subirá de nivel y evolucionará a medida que cumplas tus hábitos reales.

Esta versión es un **prototipo web** (v0.5: sprites ampliados, tres estados de Pokédex y API de
desarrollo). Se usa desde el navegador, dentro de un simulador de un dispositivo portátil con
pantalla circular de 466 × 466 px.

---

## Índice

1. [Requisitos](#1-requisitos)
2. [Instalación (solo la primera vez)](#2-instalación-solo-la-primera-vez)
3. [Arrancar la aplicación](#3-arrancar-la-aplicación)
4. [Cómo se usa](#4-cómo-se-usa)
5. [Panel de administración](#5-panel-de-administración-opcional)
6. [Solución de problemas](#6-solución-de-problemas)
7. [Para desarrolladores](#7-para-desarrolladores)
8. [Estado del proyecto y limitaciones](#8-estado-del-proyecto-y-limitaciones)

---

## 1. Requisitos

- **Windows 10 u 11** (las instrucciones usan PowerShell; en macOS/Linux ver la nota de la sección 2).
- **Python 3.12**. Para comprobarlo, abre PowerShell y escribe:

  ```powershell
  python --version
  ```

  Debe aparecer `Python 3.12.x`. Si no, descárgalo de <https://www.python.org/downloads/> y, durante
  la instalación, marca la casilla **«Add python.exe to PATH»**.
- **Un navegador moderno** (Chrome, Edge o Firefox).
- **Conexión a internet** solo para la instalación y para cargar la fuente retro. Los sprites de los
  151 Pokémon están incluidos en el proyecto, así que sin conexión la app funciona igual (con una
  fuente de sistema).

## 2. Instalación (solo la primera vez)

Abre **PowerShell** (tecla Windows → escribe «PowerShell» → Intro) y ejecuta estos comandos uno a uno:

```powershell
# 1. Entra en la carpeta del proyecto
cd C:\Users\Alex\habitmon

# 2. Crea un entorno virtual de Python (una carpeta .venv con las librerías del proyecto)
python -m venv .venv

# 3. Instala las dependencias (Django, Pillow y las herramientas de pruebas)
.\.venv\Scripts\pip install -r requirements.txt

# 4. Crea la base de datos con las 151 especies de Kanto
cd backend
..\.venv\Scripts\python manage.py migrate
```

Los sprites ya vienen en `backend/static/sprites/pokemon/`. Si faltan (por ejemplo, si los has
borrado), se regeneran con:

```powershell
..\.venv\Scripts\python manage.py build_pokedex_sprites
```

Este comando descarga los 151 sprites una sola vez y crea las miniaturas y las siluetas grises.

> **¿Vienes de una versión anterior?** Ejecuta `.\.venv\Scripts\pip install -r requirements.txt`
> y `..\.venv\Scripts\python manage.py migrate`. Tu compañero se conserva y queda registrado
> automáticamente en la Pokédex.

> Si el proyecto está en otra carpeta, cambia la ruta del primer comando.
>
> **macOS / Linux:** usa `python3 -m venv .venv`, `.venv/bin/pip` y `../.venv/bin/python`
> en lugar de las rutas con `Scripts\`.

## 3. Arrancar la aplicación

Cada vez que quieras usar HabitMon:

```powershell
cd C:\Users\Alex\habitmon\backend
..\.venv\Scripts\python manage.py runserver
```

Cuando veas el mensaje `Starting development server at http://127.0.0.1:8000/`, abre el navegador en:

**<http://127.0.0.1:8000/>**

- **Para cerrar la aplicación:** vuelve a PowerShell y pulsa `Ctrl + C`.
- **Tu partida se guarda sola** en la base de datos (`backend/db.sqlite3`). Puedes cerrar el
  navegador o apagar el ordenador: al volver a arrancar, tu compañero seguirá ahí.

## 4. Cómo se usa

La primera vez se recorren en orden las cuatro primeras pantallas. Después, desde la pantalla
principal se accede a la Pokédex con un gesto.

### Pantalla 1 · Bienvenida

Muestra el logotipo de HabitMon sobre un fondo inspirado en Game Boy. Pulsa **COMENZAR** para
elegir a tu compañero.

Si ya tienes un compañero, esta pantalla se salta y vas directamente a donde lo dejaste.

### Pantalla 2 · Elige a tu compañero

Puedes elegir entre los tres Pokémon iniciales de primera generación:

| Pokémon | Tipo |
|---|---|
| Bulbasaur | Planta / Veneno |
| Charmander | Fuego |
| Squirtle | Agua |

Para cambiar de Pokémon puedes:

- pulsar las flechas **◀** y **▶**,
- usar las **flechas del teclado** (← →),
- o **deslizar** el dedo o el ratón sobre el Pokémon hacia los lados.

Bajo el sprite se muestran su nombre y sus tipos, y los puntos indican en qué posición estás.

Cuando lo tengas claro, pulsa **ELEGIR**. Aparecerá **«¿QUIERES ELEGIR A …?»**:

- **SÍ** guarda tu elección.
- **NO** te devuelve al selector.

> La elección es definitiva: solo puedes tener un compañero. Para probar otro, consulta la
> [sección 5](#5-reiniciar-la-partida).

### Pantalla 3 · Presentación

Una Poké Ball se abre y tu Pokémon aparece con un destello. Después, un cuadro de texto retro
escribe letra a letra una breve presentación:

> ¡CHARMANDER SE UNE A TI!  
> ¡TIENES UN NUEVO COMPAÑERO!  
> JUNTOS CONSEGUIRÉIS GRANDES COSAS.  
> ¡QUE COMIENCE VUESTRA AVENTURA!

Pulsa **CONTINUAR** para pasar a la siguiente frase. Si lo pulsas mientras se escribe una frase,
esta se completa de golpe. Al terminar la última frase llegas a la pantalla principal.

Si cierras la aplicación a mitad de la presentación, al volver se reanudará desde el principio.
Una vez completada, no vuelve a aparecer.

### Pantalla 4 · Pantalla principal

Es la pantalla de tu compañero y la base de las funciones futuras. Muestra:

- su **nombre** y su **nivel** (empieza en el 1),
- su **sprite animado** sobre un paisaje pixel art,
- un panel con cuatro barras:

| Barra | Significado | Valor inicial |
|---|---|---|
| **EXP** | Experiencia acumulada | 0 (vacía) |
| **FEL** | Felicidad: su estado emocional | 70 / 100 |
| **ENE** | Energía: su disponibilidad para actividades | 100 / 100 |
| **VIN** | Vínculo: la relación y constancia contigo | 0 / 100 |

Botones:

- **INFO** abre la ficha del compañero: número de Pokédex, tipos, nivel, experiencia, valores
  exactos de bienestar y fecha en que lo elegiste. Se cierra con **CERRAR**.
- La pestaña oscura del **borde derecho** (◀) abre la Pokédex, igual que el gesto.

> En esta versión los valores aún no cambian: los hábitos, la experiencia y la evolución llegarán
> en las próximas iteraciones. Tu compañero **nunca muere ni desaparece** por no cumplir hábitos.

### Pantalla 5 · Pokédex

Tu colección de los **151 Pokémon de Kanto**, ordenados por número.

**Cómo abrirla y cerrarla:**

| Acción | Táctil o ratón | Teclado | Clic |
|---|---|---|---|
| Abrir la Pokédex (desde la pantalla principal) | Deslizar de **derecha a izquierda** | `→` | Pestaña del borde derecho |
| Recorrer la lista | Deslizar **arriba / abajo** o rueda del ratón | `↑` `↓` tras pulsar en la lista | — |
| Volver a la pantalla principal | Deslizar de **izquierda a derecha** | `←` o `Esc` | Pestaña del borde izquierdo |

> Con ratón, «deslizar» es mantener pulsado el botón izquierdo, arrastrar y soltar. El
> desplazamiento vertical nunca cambia de pantalla: solo cuentan los gestos claramente horizontales.

**Qué verás:**

- Arriba, fijos, el título **POKÉDEX**, el contador de especies desbloqueadas (por ejemplo,
  `001/151`) y una barra de progreso de la colección.
- Debajo, la lista con el número de cada especie (`#001` … `#151`). Cada especie tiene uno de
  estos tres estados:

| Estado | Imagen | Nombre | Ficha |
|---|---|---|---|
| **UNKNOWN** (desconocido) | Silueta gris | `???` | No accesible |
| **SEEN** (visto) | Silueta gris + «VISTO» | Nombre real | Reducida (número, nombre, tipos) |
| **UNLOCKED** (desbloqueado) | Sprite a color | Nombre real | Completa |

Al elegir tu compañero inicial se desbloquea **solo esa especie**; las otras 150 empiezan
en UNKNOWN. Las colecciones se guardan solas y se conservan al cerrar la aplicación.

### Ficha de una especie

- **SEEN:** número, nombre, silueta y tipos. No incluye descripción ni medidas.
- **UNLOCKED:** número, nombre, categoría, sprite animado, tipos, altura, peso, descripción y
  fecha de desbloqueo.

Para volver a la lista, desliza de izquierda a derecha, pulsa `←` / `Esc` o usa la pestaña del
borde izquierdo. La lista recupera la posición en la que estabas.

## 5. Panel de administración (opcional)

Django incluye un panel para ver y editar los datos directamente (especies y compañeros). Para
usarlo, crea primero un usuario administrador:

```powershell
cd C:\Users\Alex\habitmon\backend
..\.venv\Scripts\python manage.py createsuperuser
```

Después, con la aplicación arrancada, entra en <http://127.0.0.1:8000/admin/>. Desde ahí puedes,
por ejemplo:

- cambiar los valores de tu compañero,
- **desbloquear Pokémon a mano** para probar la Pokédex (*Entradas de Pokédex → Añadir*),
- o marcar qué especies se pueden elegir como inicial (casilla *selectable*).

## 6. Solución de problemas

| Problema | Solución |
|---|---|
| `python` no se reconoce como comando | Python no está instalado o no está en el PATH. Reinstálalo marcando «Add python.exe to PATH» y abre una ventana nueva de PowerShell. |
| `can't open file '...manage.py': [Errno 2] No such file or directory` | No estás en la carpeta correcta. Ejecuta `cd C:\Users\Alex\habitmon\backend`. |
| `..\.venv\Scripts\python` no existe | Falta el entorno virtual. Repite la [instalación](#2-instalación-solo-la-primera-vez). |
| `Error: That port is already in use` | Ya hay otra copia arrancada. Ciérrala con `Ctrl + C` o usa otro puerto: `..\.venv\Scripts\python manage.py runserver 8001` y abre <http://127.0.0.1:8001/>. |
| `no such table` al abrir la página | Falta crear la base de datos: `..\.venv\Scripts\python manage.py migrate`. |
| Los Pokémon aparecen como cuadros con una letra o imágenes rotas | Faltan los sprites. Ejecuta `..\.venv\Scripts\python manage.py build_pokedex_sprites` (necesita internet una vez). |
| `No module named 'PIL'` | Falta Pillow. Ejecuta `.\.venv\Scripts\pip install -r requirements.txt` desde la carpeta `habitmon`. |
| El gesto no abre la Pokédex | Arrastra en horizontal al menos un par de centímetros y sin desviarte mucho en vertical, o usa la pestaña del borde o la tecla `→`. |
| La página no cambia tras modificar el CSS o el JS | Recarga sin caché con `Ctrl + F5`. |

## 7. Para desarrolladores

### Tecnologías

Python 3.12, Django 5.2, SQLite, HTML, CSS y JavaScript sin frameworks (gestos con Pointer
Events); Pillow para generar miniaturas y siluetas; pruebas con pytest y pytest-django.

### Ejecutar las pruebas

```powershell
cd C:\Users\Alex\habitmon\backend
..\.venv\Scripts\python -m pytest
```

Las pruebas usan una base de datos temporal, así que no afectan a tu partida.

### Usar otra base de datos

La variable de entorno `HABITMON_DB` permite arrancar la app con otra base de datos, por ejemplo
una copia para hacer pruebas sin tocar tu partida:

```powershell
Copy-Item db.sqlite3 pruebas.sqlite3
$env:HABITMON_DB = "pruebas.sqlite3"
..\.venv\Scripts\python manage.py runserver
```

### Estructura

```text
habitmon/
├── backend/
│   ├── config/                 # Ajustes y URLs de Django
│   ├── profiles/               # Perfil local (agrupa compañero y colección)
│   ├── companions/             # Módulo del compañero
│   │   ├── models.py           # PokemonSpecies (catálogo de 151) y Companion
│   │   ├── services.py         # Reglas: elegir inicial, completar presentación, reiniciar
│   │   ├── texts.py            # Textos narrativos (editables y traducibles)
│   │   ├── data/               # kanto_species.json: nombres, tipos, medidas y descripciones
│   │   ├── views.py / urls.py  # Pantallas y rutas
│   │   ├── migrations/         # Esquema + carga del catálogo
│   │   └── tests/
│   ├── pokedex/                # Módulo de la Pokédex
│   │   ├── models.py           # PokedexEntry: SEEN y UNLOCKED (ausencia = UNKNOWN)
│   │   ├── selectors.py        # Lecturas: lista, contador, ficha (tres estados)
│   │   ├── services.py         # Escrituras: unlock, mark_seen, set_pokemon_status
│   │   ├── management/commands/build_pokedex_sprites.py
│   │   └── tests/
│   ├── development/            # Endpoints de depuración (solo con HABITMON_ALLOW_RESET=True)
│   │   ├── views.py / urls.py  # DELETE /restart · PUT /pokedex/{id}
│   │   ├── services.py
│   │   └── tests/
│   ├── templates/              # companions/ (4 pantallas) y pokedex/ (lista y ficha)
│   ├── static/css/             # device.css (simulador, estética retro), pokedex.css
│   ├── static/js/              # ui.js, gestures.js, selection.js, intro.js, pokedex.js
│   ├── static/sprites/pokemon/ # animated/, thumbs/ y silhouettes/ (151 de cada)
│   └── manage.py
├── firmware/                   # Reservado para el firmware ESP32
├── requirements.txt
└── README.md
```

### Rutas

| Ruta | Pantalla |
|---|---|
| `/` | 1 · Bienvenida (si ya hay compañero, redirige a donde se quedó) |
| `/elegir/` | 2 · Selección del inicial |
| `/presentacion/` | 3 · Presentación narrativa |
| `/companero/` | 4 · Pantalla principal |
| `/pokedex/` | 5 · Pokédex |
| `/pokedex/<número>/` | Ficha de una especie (404 si es UNKNOWN o no existe) |
| `/admin/` | Administración de Django |

### API de desarrollo

Los endpoints de depuración solo están disponibles cuando `HABITMON_ALLOW_RESET = True`
(por defecto coincide con `DEBUG` en `backend/config/settings.py`). Úsalos con Postman, curl
o desde las pruebas automatizadas. **No los expongas en producción.**

#### DELETE /restart

Restablece el estado del perfil local: borra el compañero activo y su colección de Pokédex.
El catálogo global de 151 especies no se toca. La operación es idempotente.

```bash
curl -X DELETE http://127.0.0.1:8000/restart
```

**Respuesta correcta (HTTP 200):**
```json
{ "code": 200, "message": "Application restarted successfully" }
```

**Error interno (HTTP 500):**
```json
{ "code": 500, "message": "Unable to restart application" }
```

#### PUT /pokedex/{id}

Establece el estado de un Pokémon en la Pokédex del perfil local. Permite regresiones
(UNLOCKED → SEEN, UNLOCKED → UNKNOWN) que el juego normal no admite.

| Parámetro | Ubicación | Valores |
|---|---|---|
| `id` | Query param | 1–151 (número nacional) |
| `status` | Query param | `UNKNOWN`, `SEEN`, `UNLOCKED` |

```bash
curl -X PUT "http://127.0.0.1:8000/pokedex?id=25&status=SEEN"
```

**Respuesta correcta (HTTP 200):**
```json
{ "code": 200, "message": "Pokemon status updated successfully", "pokemon_id": 25, "status": "SEEN" }
```

**Pokémon inexistente (HTTP 404):**
```json
{ "code": 404, "message": "Pokemon not found" }
```

**Estado inválido (HTTP 400):**
```json
{ "code": 400, "message": "Invalid Pokemon status" }
```

**Nota:** actualizar a `UNKNOWN` elimina el registro de la Pokédex; la especie vuelve al estado
de silhouette con `???`. Los cambios no otorgan experiencia ni desencadenan recompensas de juego.

### Cambiar los textos

Las frases de la presentación y de los avisos de confirmación están en
`backend/companions/texts.py`. `{name}` se sustituye por el nombre del Pokémon en mayúsculas.

## 8. Estado del proyecto y limitaciones

**Incluido en esta versión (v0.5):** bienvenida, selección del inicial con confirmación,
presentación narrativa, pantalla principal con nivel, experiencia y bienestar, Pokédex de
151 especies con tres estados (UNKNOWN, SEEN, UNLOCKED), sprites ampliados, navegación por
gestos, persistencia de la partida y API de desarrollo para pruebas (`DELETE /restart`,
`PUT /pokedex/{id}`).

**Todavía no incluido:** formas de desbloquear o avistar nuevas especies jugando (por ahora solo
desde el panel de administración o la API de desarrollo), hábitos, ganancia de experiencia, subida
de nivel, evoluciones, rachas, usuarios o inicio de sesión, aplicación móvil y firmware para ESP32
o smartwatch.

**Datos:** los nombres, tipos, medidas y descripciones en español proceden de
[PokeAPI](https://pokeapi.co/). Los tipos son los actuales (por ejemplo, Clefairy es de tipo Hada),
no los de los juegos originales de 1996.

**Sprites y derechos de uso:** los sprites proceden del repositorio público
[PokeAPI/sprites](https://github.com/PokeAPI/sprites) y se guardan en local, como pide el diseño.
Los diseños de Pokémon pertenecen a Nintendo, Game Freak y The Pokémon Company. Este prototipo es
de uso personal y no debe publicarse ni usarse comercialmente sin resolver antes los derechos.

**Referencia:** [TamaPoke](https://github.com/socquique/TamaPoke).
