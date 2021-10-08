package fi.lauriari.ar_project.Activities

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.StepCounter
import java.time.LocalDate

class MainActivity : AppCompatActivity() {

    val stepCounter = StepCounter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getSupportActionBar()?.hide()
        //setupActionBarWithNavController(findNavController(R.id.nav_host_fragment))

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions (this,arrayOf (Manifest.permission.ACTIVITY_RECOGNITION) , 1) ;
        }
        stepCounter.initSensorManager()
        Log.d("time","${LocalDate.now()}")
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        stepCounter.getStepCounterSensor().also {
            stepCounter.getSensorManager()
                .registerListener(stepCounter, it, SensorManager.SENSOR_DELAY_UI)
        }
        stepCounter.loadData()
    }

    override fun onPause() {
        super.onPause()
        stepCounter.getSensorManager().unregisterListener(stepCounter)
        stepCounter.savePreviousTotalSteps()
        stepCounter.saveCurrentDate()
    }
}