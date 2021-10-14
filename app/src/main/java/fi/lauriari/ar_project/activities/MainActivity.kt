package fi.lauriari.ar_project.activities

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import fi.lauriari.ar_project.*

val stepCounter = StepCounter()

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        // Star networkcallback to receive network status updates
        NetworkMonitor(application).startNetworkCallback()

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    1
                )
            }
        }
        stepCounter.initSensorManager(this)
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
        // send toast message when there is no step detector sensor in the device
        if (stepCounter.getStepCounterSensor() == null) {
            Toast.makeText(this, "Sorry, Step detector sensor is not available!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
       // stepCounter.getSensorManager().unregisterListener(stepCounter)
        stepCounter.savePreviousTotalSteps()
        stepCounter.saveCurrentDate()
    }
}