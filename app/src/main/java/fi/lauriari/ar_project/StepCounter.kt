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

class StepCounter() : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    private val sharedPrefName = "stepCounter"
    private val savedPreviousSteps = "previousSteps"
    private val savedCurrentDate = "currentDate"
    private var totalSteps = 0f
    private var previousTotalSteps = 0f
    private var currentSteps = 0
    private lateinit var sharedPreferences: SharedPreferences


    fun initSensorManager(activity: Activity) {
        sharedPreferences = activity.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        loadData()
    }

    fun getTotalSteps() = totalSteps
    fun getSensorManager() = sensorManager
    fun getStepCounterSensor() = stepCounterSensor
    fun getSavedDate()=  sharedPreferences.getString(savedCurrentDate, LocalDate.now().toString())

    fun saveCurrentDate() {
        val currentDate = LocalDate.now().toString()
        val editor = sharedPreferences.edit()
        editor.putString(savedCurrentDate, currentDate)
        editor.apply()
    }

    fun savePreviousTotalSteps() {
        previousTotalSteps = totalSteps
        val editor = sharedPreferences.edit()
        editor.putFloat(savedPreviousSteps, previousTotalSteps)
        editor.apply()
    }

    private fun loadData() {
        val savedValue = sharedPreferences.getFloat(savedPreviousSteps, 0f)
//        val savedDateString =
//            sharedPreferences.getString(savedCurrentDate, LocalDate.now().toString())
        val savedDateString = getSavedDate()
        val savedDate = LocalDate.parse(savedDateString, DateTimeFormatter.ISO_DATE)
        Log.d(
            "saved",
            " ***** saved: $savedValue, sdate: $savedDateString, previous: $previousTotalSteps"
        )

        // when the date is changed
        if (savedDate.compareTo(LocalDate.now()) != 0) {
            saveCurrentDate()
            previousTotalSteps = savedValue
        }
        Log.d("saved", "saved: $savedValue,sdata: $savedDateString, previous: $previousTotalSteps")
    }
//
//    fun resetSteps() {
//        previousTotalSteps = totalSteps
//        savePreviousTotalSteps()
//    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        p0.sensor = stepCounterSensor
        totalSteps = p0.values[0]
        val savedDate = LocalDate.parse(getSavedDate(), DateTimeFormatter.ISO_DATE)
        if(savedDate.compareTo(LocalDate.now())!=0){
            currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
        }else{
            currentSteps = totalSteps.toInt()
        }


        Log.d("steps", "total: $totalSteps, current: $currentSteps, previous:$previousTotalSteps")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}