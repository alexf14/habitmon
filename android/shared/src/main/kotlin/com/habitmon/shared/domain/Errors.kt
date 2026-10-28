package com.habitmon.shared.domain

/** Errores de dominio. Equivalen a las excepciones de companions/services.py del prototipo Django. */
sealed class HabitMonError(message: String) : Exception(message) {
    class CompanionAlreadyExists : HabitMonError("Ya existe un compañero activo")
    class InvalidStarter(speciesId: Int) : HabitMonError("La especie $speciesId no es un inicial seleccionable")
    class NoCompanion : HabitMonError("No hay compañero activo")
    class PokemonNotFound(speciesId: Int) : HabitMonError("La especie $speciesId no existe")
    class PokemonNotUnlocked(speciesId: Int) : HabitMonError("La especie $speciesId no está desbloqueada")
}
