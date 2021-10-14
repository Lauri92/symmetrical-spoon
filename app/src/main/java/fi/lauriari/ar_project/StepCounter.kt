package fi.lauriari.ar_project

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Step counter to detect steps from the user.
 * Step detector sensor is used.
 *  ACTIVITY_RECOGNITION permission must be declared in order for your app
 *  to use this sensor on devices running Android 10 (API level 29) or higher.
 */

class StepCounter : SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepDetectorSensor: Sensor? = null

    private val sharedPrefName = "stepCounter"
    private val savedPreviousSteps = "previousSteps"
    private val savedCurrentDate = "currentDate"
    private var previousTotalSteps = 0f
    private val currentSteps: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            Log.d("init", "init")
            it.value = initValue()
        }
    }

    private fun initValue() = 0
    private lateinit var sharedPreferences: SharedPreferences

    fun initSensorManager(activity: Activity) {
        sharedPreferences = activity.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE)
        sensorManager = activity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        loadData()
    }

    fun getCurrentSteps(): LiveData<Int> = currentSteps
    fun getSensorManager() = sensorManager
    fun getStepCounterSensor() = stepDetectorSensor

    private fun getSavedDate() =
        sharedPreferences.getString(savedCurrentDate, LocalDate.now().toString())

    fun saveCurrentDate() {
        val currentDate = LocalDate.now().toString()
        val editor = sharedPreferences.edit()
        editor.putString(savedCurrentDate, currentDate)
        editor.apply()
    }

    fun savePreviousTotalSteps() {
        val editor = sharedPreferences.edit()
        currentSteps.value?.let { editor.putFloat(savedPreviousSteps, it.toFloat()) }
        editor.apply()
    }

    private fun loadData() {
        val savedValue = sharedPreferences.getFloat(savedPreviousSteps, 0f)
        val savedDateString = getSavedDate()
        val savedDate = LocalDate.parse(savedDateString, DateTimeFormatter.ISO_DATE)
        currentSteps.postValue(savedValue.toInt())
        // when the date is changed
        if (savedDate.compareTo(LocalDate.now()) != 0) {
            Log.d("different", "${LocalDate.now()}")
            currentSteps.value = initValue()
        }
        Log.d("saved", "saved: $savedValue,sdata: $savedDateString, previous: $previousTotalSteps")
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        p0.sensor = stepDetectorSensor
        currentSteps.postValue(currentSteps.value?.plus(1))
        savePreviousTotalSteps()
        Log.d("steps", "current: ${currentSteps.value}, previous:$previousTotalSteps")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}