package fi.lauriari.ar_project.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import fi.lauriari.ar_project.R


class SplashActivity : AppCompatActivity() {

    // delay duration for showing the splash screen in milliseconds
    val DELAY_DURATION = 2000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen);
        getSupportActionBar()?.hide();
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, DELAY_DURATION
        )
    }

}