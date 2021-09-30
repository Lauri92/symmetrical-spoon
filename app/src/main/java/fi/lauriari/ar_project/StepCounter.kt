package fi.lauriari.ar_project

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class StepCounter : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    private var totalSteps = 0

    fun initSensor(activity: Activity) {
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }

    fun getTotalSteps() = totalSteps
    fun getSensorManager() = sensorManager
    fun getStepCounterSensor() = stepCounterSensor

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        p0.sensor == stepCounterSensor
        totalSteps = (p0.values[0]).toInt()
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}