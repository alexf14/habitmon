package com.habitmon.shared.domain

/** Objetivos diarios. Mismos valores que activity/services.py del prototipo Django. */
object ActivityGoals {
    const val STEPS = 10_000
    const val KILOMETERS = 8.0

    fun stepProgress(steps: Int): Float = (steps.toFloat() / STEPS).coerceIn(0f, 1f)

    fun distanceProgress(kilometers: Double): Float = (kilometers / KILOMETERS).toFloat().coerceIn(0f, 1f)
}
