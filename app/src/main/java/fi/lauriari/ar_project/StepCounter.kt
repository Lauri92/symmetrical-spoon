package fi.lauriari.ar_project

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Step counter to detect steps from the user.
 * Step detector sensor is used.
 *  ACTIVITY_RECOGNITION permission must be declared in order for your app
 *  to use this sensor on devices running Android 10 (API level 29) or higher.
 */

class StepCounter() : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepDetectorSensor: Sensor? = null

    private val sharedPrefName = "stepCounter"
    private val savedPreviousSteps = "previousSteps"
    private val savedCurrentDate = "currentDate"
   // private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var currentSteps = 0
    private lateinit var sharedPreferences: SharedPreferences


    fun initSensorManager(activity: Activity) {
        sharedPreferences = activity.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        loadData()
    }

    fun getCurrentSteps() = currentSteps
    fun getSensorManager() = sensorManager
    fun getStepCounterSensor() = stepDetectorSensor
    fun getSavedDate() = sharedPreferences.getString(savedCurrentDate, LocalDate.now().toString())

    fun saveCurrentDate() {
        val currentDate = LocalDate.now().toString()
        val editor = sharedPreferences.edit()
        editor.putString(savedCurrentDate, currentDate)
        editor.apply()
    }

    fun savePreviousTotalSteps() {
        //previousTotalSteps = totalSteps
        val editor = sharedPreferences.edit()
        editor.putFloat(savedPreviousSteps, currentSteps.toFloat())
        editor.apply()
    }

    private fun loadData() {
        val savedValue = sharedPreferences.getFloat(savedPreviousSteps, 0f)
        val savedDateString = getSavedDate()
        val savedDate = LocalDate.parse(savedDateString, DateTimeFormatter.ISO_DATE)
        currentSteps = savedValue.toInt()
        Log.d(
            "saved",
            " ***** saved: $savedValue, sdate: $savedDateString, previous: $previousTotalSteps"
        )

        // when the date is changed
        if (savedDate.compareTo(LocalDate.now()) != 0) {

            Log.d("different", "${LocalDate.now()}")
           currentSteps = 0
        }
        Log.d("saved", "saved: $savedValue,sdata: $savedDateString, previous: $previousTotalSteps")
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        p0.sensor = stepDetectorSensor
        currentSteps++
        savePreviousTotalSteps()
        Log.d("steps", "current: $currentSteps, previous:$previousTotalSteps")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}