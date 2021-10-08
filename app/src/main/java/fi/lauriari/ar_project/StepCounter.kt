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

class StepCounter(private val activity: Activity) : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    private val SHARED_PREF_NAME = "stepCounter"
    private val PREF_VALUE = "previousSteps"
    private val CURRENT_DATE_VALUE = "currentDate"
    private var running = false
    private var totalSteps = 0f
    var previousTotalSteps = 0f
    private lateinit var sharedPreferences: SharedPreferences


    fun initSensorManager() {
        //loadData(activity)
        sharedPreferences = activity.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    fun getTotalSteps() = totalSteps
    fun getSensorManager() = sensorManager
    fun getStepCounterSensor() = stepCounterSensor

    fun saveCurrentDate() {
        val currentDate = LocalDate.now().toString()
        val editor = sharedPreferences.edit()
        editor.putString(CURRENT_DATE_VALUE, currentDate)
        editor.apply()
    }

    fun savePreviousTotalSteps() {
        previousTotalSteps = totalSteps
        val editor = sharedPreferences.edit()
        editor.putFloat(PREF_VALUE, previousTotalSteps)
        editor.apply()
    }

    fun loadData() {
        //val sharedPreferences = activity.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val savedValue = sharedPreferences.getFloat(PREF_VALUE, 0f)
//        val savedDateString = sharedPreferences.getString(CURRENT_DATE_VALUE, "")
//        val savedDate = LocalDate.parse(savedDateString, DateTimeFormatter.ISO_DATE)

        // when the date is changed
//        if (totalSteps != 0f && savedDate.compareTo(LocalDate.now()) != 0) {
//            saveCurrentDate()
//            previousTotalSteps = savedValue
//        }
        Log.d("saved", "$savedValue, ${previousTotalSteps}")
    }

    fun resetSteps() {
        previousTotalSteps = totalSteps
        savePreviousTotalSteps()
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        p0.sensor = stepCounterSensor
        totalSteps = p0.values[0]
        val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
        Log.d("steps", "$totalSteps, $currentSteps")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}