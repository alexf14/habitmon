# HabitMon Wear OS: herramientas de depuración

Solo existen en builds de **debug**. El build de release no incluye el receptor ADB y no muestra el botón DBG.

## Pantalla de debug en el reloj

En la pantalla principal del compañero, pulsa **DBG**. Desde ahí puedes:

- Sumar pasos (+1000, +5000) y kilómetros (+1).
- Desbloquear especies.
- Cambiar de compañero.
- Hacer un reset completo.

## Comandos ADB desde el ordenador

Con el emulador o el reloj conectado (`adb devices`), ejecuta:

```sh
R=com.habitmon.wear/.debug.DebugReceiver

# Pasos y distancia de hoy
adb shell am broadcast -n $R -a com.habitmon.debug.ADD_STEPS --ei count 5000
adb shell am broadcast -n $R -a com.habitmon.debug.SET_STEPS --ei steps 12000
adb shell am broadcast -n $R -a com.habitmon.debug.SET_KM --ef km 3.5

# Pokédex: UNLOCKED por defecto; también acepta status SEEN o UNKNOWN
adb shell am broadcast -n $R -a com.habitmon.debug.UNLOCK_POKEMON --ei pokemon_id 25
adb shell am broadcast -n $R -a com.habitmon.debug.UNLOCK_POKEMON --ei pokemon_id 25 --es status SEEN

# Cambiar de compañero (lo desbloquea antes si hace falta)
adb shell am broadcast -n $R -a com.habitmon.debug.SET_COMPANION --ei pokemon_id 25

# Borra compañero, Pokédex y actividad (conserva el catálogo)
adb shell am broadcast -n $R -a com.habitmon.debug.RESET
```

En PowerShell, cambia `R=...` por `$R = "com.habitmon.wear/.debug.DebugReceiver"`.

El resultado aparece en la línea `data=` de la salida del comando, por ejemplo `data="OK +5000 pasos"` o `data="ERROR ..."`. También se registra en logcat con la etiqueta `HabitMonDebug`:

```sh
adb logcat -s HabitMonDebug
```

El receptor exige el permiso `DUMP`, que tiene `adb shell` pero no las demás apps instaladas en el reloj.

## Equivalencia con el prototipo Django

| Django                                | Wear OS                      |
|---------------------------------------|------------------------------|
| `/restart`                            | `RESET`                      |
| `/pokedex` (cambiar estado)           | `UNLOCK_POKEMON` + `status`  |
| `PUT /activity/daily`                 | `SET_STEPS` / `SET_KM`       |
| `POST /activity/daily/steps/increment`| `ADD_STEPS`                  |
| `PUT /companions/active`              | `SET_COMPANION`              |
