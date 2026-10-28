package com.habitmon.wear.health

import android.content.Context
import android.util.Log
import com.habitmon.shared.data.repository.ActivityRepository

/**
 * Wraps Health Services PassiveMonitoringClient for step tracking.
 *
 * In debug builds, steps can be injected via DebugReceiver or the debug screen
 * without requiring actual sensor hardware.
 *
 * TODO: Implement actual Health Services integration:
 *   val healthClient = HealthServices.getClient(context)
 *   val passiveClient = healthClient.passiveMonitoringClient
 *   passiveClient.setPassiveListenerCallback(
 *       PassiveListenerConfig.builder()
 *           .setDataTypes(setOf(DataType.STEPS_DAILY))
 *           .build(),
 *       callback
 *   )
 *
 * Requires runtime permissions: BODY_SENSORS, ACTIVITY_RECOGNITION.
 */
class StepTracker(
    private val context: Context,
    private val activityRepository: ActivityRepository
) {
    private var tracking = false

    fun startTracking() {
        if (tracking) return
        tracking = true
        Log.d(TAG, "Step tracking started (stub — use debug tools to inject steps)")
        // TODO: Register passive listener with Health Services
    }

    fun stopTracking() {
        if (!tracking) return
        tracking = false
        Log.d(TAG, "Step tracking stopped")
        // TODO: Unregister passive listener
    }

    val isTracking: Boolean get() = tracking

    companion object {
        private const val TAG = "StepTracker"
    }
}
