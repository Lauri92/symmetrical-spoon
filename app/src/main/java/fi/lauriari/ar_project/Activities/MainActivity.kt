package fi.lauriari.ar_project.Activities

import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import fi.lauriari.ar_project.ImageFatcher
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.StepCounter

class MainActivity : AppCompatActivity() {

    val stepCounter = StepCounter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()
        setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))
        stepCounter.initSensor(this)
        Log.d("step counter" ,"${stepCounter.getStepCounterSensor()}")

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        stepCounter.getStepCounterSensor().also { stepCounter.getSensorManager().registerListener(stepCounter, it, SensorManager.SENSOR_DELAY_NORMAL) }
    }

    override fun onPause() {
        super.onPause()
        stepCounter.getSensorManager().unregisterListener(stepCounter)
    }
}